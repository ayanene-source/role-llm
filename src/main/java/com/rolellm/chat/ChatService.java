package com.rolellm.chat;

import com.rolellm.chat.dto.ChatRequest;
import com.rolellm.chat.dto.ChatResponse;
import com.rolellm.chat.memory.ChatMemoryService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final LlmClient llmClient;
    private final RolePromptProvider rolePromptProvider;
    private final ChatMemoryService chatMemoryService;

    public ChatService(
            LlmClient llmClient,
            @Qualifier("default") RolePromptProvider rolePromptProvider,
            ChatMemoryService chatMemoryService) {
        this.llmClient = llmClient;
        this.rolePromptProvider = rolePromptProvider;
        this.chatMemoryService = chatMemoryService;
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        String conversationId = chatMemoryService.getOrCreateConversationId(chatRequest.getConversationId());
        List<PromptMessage> historyMessages = chatMemoryService.getHistory(conversationId);
        List<PromptMessage> messages = new ArrayList<>();

        // 添加系统提示词（可选）
        Optional<String> optionalPrompt = rolePromptProvider.currentSystemPrompt();
        if (optionalPrompt.isPresent()) {
            String systemPrompt = optionalPrompt.get();
            PromptMessage p = new PromptMessage(MessageRole.SYSTEM.value(), systemPrompt);
            messages.add(p);
        }

        // 添加当前会话历史，让模型具备本轮内的长对话记忆
        messages.addAll(historyMessages);

        // 添加用户消息：将用户的输入消息包装成 USER 角色的消息并添加到列表
        PromptMessage t = new PromptMessage(MessageRole.USER.value(), chatRequest.getMessage());
        messages.add(t);

        log.info("Prompt messages prepared: conversationId={}, historyMessages={}, totalMessages={}",
                conversationId,
                historyMessages.size(),
                messages.size());

        // 构建 LLM 请求对象
        LlmChatRequest request = new LlmChatRequest(messages);

        // 调用大模型 API
        LlmChatResult result = llmClient.chat(request);

        // 追加消息到记忆
        chatMemoryService.appendUserAndAssistant(
                conversationId,
                chatRequest.getMessage(),
                result.getReply());

        log.info("model={}, replyLength={}, usage={}, conversationId={}",
                result.getModel(),
                result.getReply().length(),
                result.getUsage(),
                conversationId);
        return new ChatResponse(result.getReply(), result.getModel(), result.getUsage(), conversationId);
    }
}
