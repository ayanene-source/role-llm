package com.rolellm.tts;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final TtsProperties properties;

    public AudioController(TtsProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> audio(@PathVariable String fileName) throws MalformedURLException {
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid audio file name");
        }

        Path outputDir = Path.of(properties.getOutputDir()).toAbsolutePath().normalize();
        Path audioPath = outputDir.resolve(fileName).normalize();
        if (!audioPath.startsWith(outputDir) || !Files.isRegularFile(audioPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Audio file not found");
        }

        UrlResource resource = new UrlResource(audioPath.toUri());
        return ResponseEntity.ok()
                .contentType(resolveContentType(fileName))
                .body(resource);
    }

    private MediaType resolveContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".wav")) {
            return MediaType.parseMediaType("audio/wav");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
