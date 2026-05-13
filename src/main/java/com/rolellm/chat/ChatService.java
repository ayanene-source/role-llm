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

        Optional<String> optionalPrompt = rolePromptProvider.currentSystemPrompt();
        if (optionalPrompt.isPresent()) {
            String systemPrompt = optionalPrompt.get();
            messages.add(new PromptMessage(MessageRole.SYSTEM.value(), systemPrompt));
        }

        messages.add(new PromptMessage(MessageRole.USER.value(), message));

        log.info("Prompt messages prepared: count={}, roles={}",
                messages.size(),
                messages.stream().map(PromptMessage::role).toList());

        LlmChatResult result = llmClient.chat(new LlmChatRequest(messages));
        log.info("Chat response ready: model={}, replyLength={}, usage={}",
                result.model(),
                result.reply().length(),
                result.usage());
        return new ChatResponse(result.reply(), result.model(), result.usage());
    }
}
