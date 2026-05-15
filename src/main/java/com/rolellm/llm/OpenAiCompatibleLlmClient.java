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

    // 构造器依赖：LLM 配置和 HTTP 客户端构建器由 Spring 注入
    private final LlmProperties properties; // LLM 配置：API Key、模型名等
    private final RestClient.Builder restClientBuilder; // HTTP 客户端构建器

    public OpenAiCompatibleLlmClient(LlmProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public LlmChatResult chat(LlmChatRequest request) {
        validateConfiguration(); // 检查 baseUrl、apiKey、model 是否配置

        ChatCompletionRequest body = new ChatCompletionRequest(
                properties.model(),              // 模型名称
                request.getMessages(),           // 消息列表：系统提示词 + 历史消息 + 用户消息
                properties.resolvedTemperature() // 温度参数：控制随机性
        );

        long start = System.nanoTime(); // 记录开始时间
        log.info("Calling LLM API: baseUrl={}, endpoint=/chat/completions, requestModel={}, temperature={}, messageCount={}",
                trimTrailingSlash(properties.baseUrl()),
                properties.model(),
                properties.resolvedTemperature(),
                request.getMessages().size());

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
                    result.getModel(),
                    elapsedMs,
                    result.getUsage());
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
        if (firstChoice == null || firstChoice.message() == null || isBlank(firstChoice.message().getContent())) {
            throw new LlmApiException("LLM API did not return a valid reply");
        }
        return new LlmChatResult(
                firstChoice.message().getContent(),
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
            String model,                 // 模型名称
            List<PromptMessage> messages, // 消息列表
            Double temperature            // 温度参数
    ) {
    }

    private record ChatCompletionResponse(
            String model,             // 使用的模型
            List<ChatChoice> choices, // 候选回复列表
            Map<String, Object> usage // Token 使用情况
    ) {
    }

    private record ChatChoice(
            PromptMessage message // AI 的回复消息
    ) {
    }
}
