package com.rolellm.chat.memory;

import com.rolellm.llm.PromptMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatMemoryService {

    private static final Logger log = LoggerFactory.getLogger(ChatMemoryService.class);
    private static final int MAX_HISTORY_MESSAGES = 20;
    private static final int MAX_SESSIONS = 100;

    private final ConcurrentMap<String, ChatSession> sessions = new ConcurrentHashMap<>();

    public String getOrCreateConversationId(String conversationId) {
        String resolvedConversationId = normalizeConversationId(conversationId);
        ChatSession session = sessions.computeIfAbsent(resolvedConversationId, ChatSession::new);
        touch(session);
        cleanupOldSessions();
        return resolvedConversationId;
    }

    public List<PromptMessage> getHistory(String conversationId) {
        ChatSession session = sessions.get(conversationId);
        if (session == null) {
            return new ArrayList<>();
        }

        synchronized (session) {
            touch(session);
            List<PromptMessage> copy = new ArrayList<>();
            for (PromptMessage message : session.getMessages()) {
                copy.add(new PromptMessage(message.getRole(), message.getContent()));
            }
            return copy;
        }
    }

    public void appendUserAndAssistant(String conversationId, String userMessage, String assistantReply) {
        ChatSession session = sessions.computeIfAbsent(conversationId, ChatSession::new);
        synchronized (session) {
            session.getMessages().add(new PromptMessage("user", userMessage));
            session.getMessages().add(new PromptMessage("assistant", assistantReply));
            trimHistory(session);
            touch(session);
            log.info("Chat memory updated: conversationId={}, historyMessages={}",
                    conversationId,
                    session.getMessages().size());
        }
    }

    private String normalizeConversationId(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return conversationId.trim();
    }

    private void trimHistory(ChatSession session) {
        while (session.getMessages().size() > MAX_HISTORY_MESSAGES) {
            session.getMessages().remove(0);
        }
    }

    private void touch(ChatSession session) {
        session.setLastAccessAt(Instant.now());
    }

    private void cleanupOldSessions() {
        if (sessions.size() <= MAX_SESSIONS) {
            return;
        }

        List<ChatSession> orderedSessions = new ArrayList<>(sessions.values());
        orderedSessions.sort(Comparator.comparing(ChatSession::getLastAccessAt));
        int removeCount = sessions.size() - MAX_SESSIONS;

        for (int i = 0; i < removeCount; i++) {
            ChatSession session = orderedSessions.get(i);
            sessions.remove(session.getConversationId());
            log.info("Old chat session removed: conversationId={}", session.getConversationId());
        }
    }
}
