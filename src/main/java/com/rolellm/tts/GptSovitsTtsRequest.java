package com.rolellm.tts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GptSovitsTtsRequest {

    private String text;

    @JsonProperty("text_lang")
    private String textLang;

    @JsonProperty("ref_audio_path")
    private String refAudioPath;

    @JsonProperty("prompt_text")
    private String promptText;

    @JsonProperty("prompt_lang")
    private String promptLang;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("streaming_mode")
    private boolean streamingMode;

    @JsonProperty("text_split_method")
    private String textSplitMethod;

    @JsonProperty("speed_factor")
    private double speedFactor;

    @JsonProperty("top_k")
    private int topK;

    @JsonProperty("top_p")
    private double topP;

    private double temperature;

    @JsonProperty("fragment_interval")
    private double fragmentInterval;

    public GptSovitsTtsRequest() {
    }

    public GptSovitsTtsRequest(TtsProperties properties, String text) {
        this.text = text;
        this.textLang = properties.getTextLang();
        this.refAudioPath = properties.getRefAudioPath();
        this.promptText = properties.getPromptText();
        this.promptLang = properties.getPromptLang();
        this.mediaType = properties.getMediaType();
        this.streamingMode = properties.isStreamingMode();
        this.textSplitMethod = properties.getTextSplitMethod();
        this.speedFactor = properties.getSpeedFactor();
        this.topK = properties.getTopK();
        this.topP = properties.getTopP();
        this.temperature = properties.getTemperature();
        this.fragmentInterval = properties.getFragmentInterval();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextLang() {
        return textLang;
    }

    public void setTextLang(String textLang) {
        this.textLang = textLang;
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
}
