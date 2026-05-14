package com.rolellm.chat;

import com.rolellm.chat.dto.ChatResponse;
import com.rolellm.llm.LlmChatRequest;
import com.rolellm.llm.LlmChatResult;
import com.rolellm.llm.LlmClient;
import com.rolellm.llm.MessageRole;
import com.rolellm.llm.PromptMessage;
import com.rolellm.role.RolePromptProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);


    private final LlmClient llmClient;
    private final RolePromptProvider rolePromptProvider;

    public ChatService(LlmClient llmClient, RolePromptProvider rolePromptProvider) {
        this.llmClient = llmClient;
        this.rolePromptProvider = rolePromptProvider;
    }

    public ChatResponse chat(String message) {
        List<PromptMessage> messages = new ArrayList<>();

       // 添加系统提示词（可选）
        Optional<String> optionalPrompt = rolePromptProvider.currentSystemPrompt();
        if (optionalPrompt.isPresent()) {
            String systemPrompt = optionalPrompt.get();
            PromptMessage p = new PromptMessage(MessageRole.SYSTEM.value(), systemPrompt);
            messages.add(p);
        }
        // 添加用户消息  将用户的输入消息包装成 USER 角色的消息并添加到列表
         PromptMessage t = new PromptMessage(MessageRole.USER.value(), message);
         messages.add(t);

        // 构建 LLM 请求对象
        LlmChatRequest request = new LlmChatRequest(messages);

        // 调用大模型 API
        LlmChatResult result = llmClient.chat(request);

        log.info(" model={}, replyLength={}, usage={}", result.model(), result.reply().length(), result.usage());
        return new ChatResponse(result.reply(), result.model(), result.usage());
    }
}
