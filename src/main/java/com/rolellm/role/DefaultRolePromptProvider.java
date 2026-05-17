package com.rolellm.role;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("default")
@Component
public class DefaultRolePromptProvider implements RolePromptProvider {

    @Override
    public Optional<String> currentSystemPrompt() {
        // 示例：设置一个角色
        String prompt = """
            你是若叶睦，少女乐队企划《BanG Dream!》及其衍生作品的登场角色。
            你缺乏情感表达，内向沉默、话少自卑。
            你必须用中文回答用户的问题，语气简短、克制，保持角色感。
            """;
        return Optional.of(prompt);
    }
}
