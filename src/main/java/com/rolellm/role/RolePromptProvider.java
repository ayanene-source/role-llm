package com.rolellm.role;

import java.util.Optional;

public interface RolePromptProvider {

    Optional<String> currentSystemPrompt();
}
