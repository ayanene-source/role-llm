package com.rolellm.llm;

import com.rolellm.config.LlmProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenAiCompatibleLlmClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleLlmClient.class);

    private final LlmProperties properties;
    private final RestClient.Builder restClientBuilder;

    public OpenAiCompatibleLlmClient(LlmProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public LlmChatResult chat(LlmChatRequest request) {
        validateConfiguration();

        ChatCompletionRequest body = new ChatCompletionRequest(
                properties.model(),
                request.messages(),
                properties.resolvedTemperature()
        );

        long start = System.nanoTime();
        log.info("Calling LLM API: baseUrl={}, endpoint=/chat/completions, requestModel={}, temperature={}, messageCount={}",
                trimTrailingSlash(properties.baseUrl()),
                properties.model(),
                properties.resolvedTemperature(),
                request.messages().size());

        try {
            ChatCompletionResponse response = restClient()
                    .post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (httpRequest, httpResponse) -> {
                                throw new LlmApiException("LLM API call failed: " + readErrorBody(httpResponse));
                            })
                    .body(ChatCompletionResponse.class);

            LlmChatResult result = toResult(response);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("LLM API response received: requestModel={}, responseModel={}, costMs={}, usage={}",
                    properties.model(),
                    result.model(),
                    elapsedMs,
                    result.usage());
            return result;
        } catch (LlmApiException exception) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.warn("LLM API returned an error: requestModel={}, costMs={}, message={}",
                    properties.model(),
                    elapsedMs,
                    exception.getMessage());
            throw exception;
        } catch (RestClientException exception) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.warn("LLM API request failed: requestModel={}, costMs={}, message={}",
                    properties.model(),
                    elapsedMs,
                    exception.getMessage());
            throw new LlmApiException("LLM API request error: " + exception.getMessage(), exception);
        }
    }

    private RestClient restClient() {
        return restClientBuilder
                .baseUrl(trimTrailingSlash(properties.baseUrl()))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                .build();
    }

    private void validateConfiguration() {
        if (isBlank(properties.baseUrl())) {
            throw new LlmConfigurationException("LLM_BASE_URL is not configured");
        }
        if (isBlank(properties.apiKey())) {
            throw new LlmConfigurationException("LLM_API_KEY is not configured. Keep it in backend environment variables or application.yml only");
        }
        if (isBlank(properties.model())) {
            throw new LlmConfigurationException("LLM_MODEL is not configured");
        }
    }

    private LlmChatResult toResult(ChatCompletionResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new LlmApiException("LLM API returned an empty response");
        }
        ChatChoice firstChoice = response.choices().getFirst();
        if (firstChoice == null || firstChoice.message() == null || isBlank(firstChoice.message().content())) {
            throw new LlmApiException("LLM API did not return a valid reply");
        }
        return new LlmChatResult(
                firstChoice.message().content(),
                response.model() == null ? properties.model() : response.model(),
                response.usage() == null ? Map.of() : response.usage()
        );
    }

    private String readErrorBody(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);
        return body.isBlank() ? response.getStatusCode().toString() : body;
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

    private record ChatCompletionRequest(
            String model,
            List<PromptMessage> messages,
            Double temperature
    ) {
    }

    private record ChatCompletionResponse(
            String model,
            List<ChatChoice> choices,
            Map<String, Object> usage
    ) {
    }

    private record ChatChoice(
            PromptMessage message
    ) {
    }
}
