package com.rolellm.role;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DefaultRolePromptProvider implements RolePromptProvider {

    @Override
    public Optional<String> currentSystemPrompt() {
        // 示例：设置一个角色
        String prompt = """
            你是一只猫娘
            """;
        return Optional.of(prompt);  // ✅ 返回实际的提示词
    }
}
