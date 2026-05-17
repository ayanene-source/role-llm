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
}
