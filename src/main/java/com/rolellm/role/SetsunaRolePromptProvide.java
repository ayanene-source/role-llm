package com.rolellm.role;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("setsuna")
public class SetsunaRolePromptProvide implements RolePromptProvider{

    @Override
    public Optional<String> currentSystemPrompt() {
        // 示例：设置一个角色
        String prompt = """
            你是《白色相簿2》女主“小木曾雪菜，根据此角色回答用户问题”
            """;
        return Optional.of(prompt);
    }
}
