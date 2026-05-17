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
    private String gptWeightsPath;
    private String sovitsWeightsPath;
    private String textSplitMethod = "cut4";
    private double speedFactor = 1.0;
    private int topK = 15;
    private double topP = 1.0;
    private double temperature = 1.0;
    private double fragmentInterval = 0.3;
    private long audioTtlMinutes = 60;
    private int audioMaxFiles = 100;

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

    public String getGptWeightsPath() {
        return gptWeightsPath;
    }

    public void setGptWeightsPath(String gptWeightsPath) {
        this.gptWeightsPath = gptWeightsPath;
    }

    public String getSovitsWeightsPath() {
        return sovitsWeightsPath;
    }

    public void setSovitsWeightsPath(String sovitsWeightsPath) {
        this.sovitsWeightsPath = sovitsWeightsPath;
    }

    public String getTextSplitMethod() {
        return textSplitMethod;
    }

    public void setTextSplitMethod(String textSplitMethod) {
        this.textSplitMethod = textSplitMethod;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public double getTopP() {
        return topP;
    }

    public void setTopP(double topP) {
        this.topP = topP;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFragmentInterval() {
        return fragmentInterval;
    }

    public void setFragmentInterval(double fragmentInterval) {
        this.fragmentInterval = fragmentInterval;
    }

    public long getAudioTtlMinutes() {
        return audioTtlMinutes;
    }

    public void setAudioTtlMinutes(long audioTtlMinutes) {
        this.audioTtlMinutes = audioTtlMinutes;
    }

    public int getAudioMaxFiles() {
        return audioMaxFiles;
    }

    public void setAudioMaxFiles(int audioMaxFiles) {
        this.audioMaxFiles = audioMaxFiles;
    }
}
