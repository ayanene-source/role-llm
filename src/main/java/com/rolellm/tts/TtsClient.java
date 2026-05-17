package com.rolellm.tts;

import java.util.Optional;

public interface TtsClient {

    Optional<String> synthesize(String text);
}
