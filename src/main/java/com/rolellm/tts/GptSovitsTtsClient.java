package com.rolellm.tts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GptSovitsTtsClient implements TtsClient {

    private static final Logger log = LoggerFactory.getLogger(GptSovitsTtsClient.class);

    private final TtsProperties properties;
    private final RestClient.Builder restClientBuilder;
    private final AtomicBoolean weightsLoaded = new AtomicBoolean(false);

    public GptSovitsTtsClient(TtsProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public Optional<String> synthesize(String text) {
        if (!properties.isEnabled()) {
            return Optional.empty();
        }
        if (isBlank(properties.getRefAudioPath()) || isBlank(properties.getPromptText())) {
            log.warn("TTS is enabled but refAudioPath or promptText is not configured");
            return Optional.empty();
        }

        long start = System.nanoTime();
        try {
            ensureWeightsLoaded();
            byte[] audioBytes = restClient()
                    .post()
                    .uri("/tts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GptSovitsTtsRequest(properties, text))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new TtsException("GPT-SoVITS API call failed: " + readErrorBody(response));
                            })
                    .body(byte[].class);

            if (audioBytes == null || audioBytes.length == 0) {
                throw new TtsException("GPT-SoVITS returned empty audio");
            }

            String fileName = saveAudio(audioBytes);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("TTS audio generated: fileName={}, size={}, costMs={}",
                    fileName,
                    audioBytes.length,
                    elapsedMs);
            return Optional.of("/api/audio/" + fileName);
        } catch (TtsException exception) {
            log.warn("TTS generation skipped: {}", exception.getMessage());
            return Optional.empty();
        } catch (IOException | RestClientException exception) {
            log.warn("TTS generation failed: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    private void ensureWeightsLoaded() {
        if (weightsLoaded.get()) {
            return;
        }
        synchronized (weightsLoaded) {
            if (weightsLoaded.get()) {
                return;
            }
            loadWeights();
            weightsLoaded.set(true);
        }
    }

    private void loadWeights() {
        if (isBlank(properties.getGptWeightsPath()) && isBlank(properties.getSovitsWeightsPath())) {
            log.info("GPT-SoVITS weight paths are not configured, using current API weights");
            return;
        }
        RestClient client = restClient();
        if (!isBlank(properties.getGptWeightsPath())) {
            log.info("Loading GPT-SoVITS GPT weights: {}", properties.getGptWeightsPath());
            client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/set_gpt_weights")
                            .queryParam("weights_path", properties.getGptWeightsPath())
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new TtsException("Set GPT weights failed: " + readErrorBody(response));
                            })
                    .toBodilessEntity();
        }
        if (!isBlank(properties.getSovitsWeightsPath())) {
            log.info("Loading GPT-SoVITS SoVITS weights: {}", properties.getSovitsWeightsPath());
            client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/set_sovits_weights")
                            .queryParam("weights_path", properties.getSovitsWeightsPath())
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new TtsException("Set SoVITS weights failed: " + readErrorBody(response));
                            })
                    .toBodilessEntity();
        }
        log.info("GPT-SoVITS weights loaded");
    }

    private RestClient restClient() {
        return restClientBuilder
                .baseUrl(trimTrailingSlash(properties.getBaseUrl()))
                .build();
    }

    private String saveAudio(byte[] audioBytes) throws IOException {
        String mediaType = normalizeMediaType(properties.getMediaType());
        String fileName = UUID.randomUUID() + "." + mediaType;
        Path outputDir = Path.of(properties.getOutputDir()).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        Files.write(outputDir.resolve(fileName), audioBytes);
        cleanupOldAudioFiles(outputDir);
        return fileName;
    }

    private void cleanupOldAudioFiles(Path outputDir) {
        try {
            int deleted = deleteExpiredFiles(outputDir) + deleteOverflowFiles(outputDir);
            int remaining = countRegularFiles(outputDir);
            log.info("TTS audio cleanup finished: deleted={}, remaining={}", deleted, remaining);
        } catch (IOException exception) {
            log.warn("TTS audio cleanup failed: {}", exception.getMessage());
        }
    }

    private int deleteExpiredFiles(Path outputDir) throws IOException {
        long ttlMinutes = properties.getAudioTtlMinutes();
        if (ttlMinutes <= 0) {
            return 0;
        }

        Instant expiresBefore = Instant.now().minus(ttlMinutes, ChronoUnit.MINUTES);
        int deleted = 0;
        try (Stream<Path> paths = Files.list(outputDir)) {
            for (Path path : paths.toList()) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }
                FileTime lastModifiedTime = Files.getLastModifiedTime(path);
                if (lastModifiedTime.toInstant().isBefore(expiresBefore) && deleteFile(path)) {
                    deleted++;
                }
            }
        }
        return deleted;
    }

    private int deleteOverflowFiles(Path outputDir) throws IOException {
        int maxFiles = properties.getAudioMaxFiles();
        if (maxFiles <= 0) {
            return 0;
        }

        List<Path> files = listAudioFiles(outputDir);
        int overflow = files.size() - maxFiles;
        if (overflow <= 0) {
            return 0;
        }

        int deleted = 0;
        for (Path path : files) {
            if (deleted >= overflow) {
                break;
            }
            if (deleteFile(path)) {
                deleted++;
            }
        }
        return deleted;
    }

    private List<Path> listAudioFiles(Path outputDir) throws IOException {
        ArrayList<Path> files = new ArrayList<>();
        try (Stream<Path> paths = Files.list(outputDir)) {
            for (Path path : paths.toList()) {
                if (Files.isRegularFile(path)) {
                    files.add(path);
                }
            }
        }
        files.sort(Comparator.comparing(this::lastModifiedTimeOrEpoch));
        return files;
    }

    private int countRegularFiles(Path outputDir) throws IOException {
        try (Stream<Path> paths = Files.list(outputDir)) {
            return (int) paths.filter(Files::isRegularFile).count();
        }
    }

    private boolean deleteFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException exception) {
            log.warn("Failed to delete TTS audio file {}: {}", path, exception.getMessage());
            return false;
        }
    }

    private FileTime lastModifiedTimeOrEpoch(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException exception) {
            return FileTime.from(Instant.EPOCH);
        }
    }

    private String readErrorBody(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);
        return body.isBlank() ? response.getStatusCode().toString() : body;
    }

    private String normalizeMediaType(String mediaType) {
        if (isBlank(mediaType)) {
            return "wav";
        }
        return mediaType.trim().toLowerCase();
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
