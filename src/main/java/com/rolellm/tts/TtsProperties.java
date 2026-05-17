package com.rolellm.tts;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tts")
public class TtsProperties {

    private boolean enabled;
    private String baseUrl = "http://127.0.0.1:9880";
    private String textLang = "ja";
    private String promptLang = "ja";
    private String mediaType = "wav";
    private boolean streamingMode;
    private String refAudioPath;
    private String promptText;
    private String outputDir = "data/audio";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTextLang() {
        return textLang;
    }

    public void setTextLang(String textLang) {
        this.textLang = textLang;
    }

    public String getPromptLang() {
        return promptLang;
    }

    public void setPromptLang(String promptLang) {
        this.promptLang = promptLang;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isStreamingMode() {
        return streamingMode;
    }

    public void setStreamingMode(boolean streamingMode) {
        this.streamingMode = streamingMode;
    }

    public String getRefAudioPath() {
        return refAudioPath;
    }

    public void setRefAudioPath(String refAudioPath) {
        this.refAudioPath = refAudioPath;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
