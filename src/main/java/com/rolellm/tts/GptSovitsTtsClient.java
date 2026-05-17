package com.rolellm.tts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
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
        return fileName;
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
