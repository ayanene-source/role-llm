package com.rolellm.role;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DefaultRolePromptProvider implements RolePromptProvider {

    @Override
    public Optional<String> currentSystemPrompt() {
        return Optional.empty();
    }
}
