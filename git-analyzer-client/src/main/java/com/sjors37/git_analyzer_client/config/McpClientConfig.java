package com.sjors37.git_analyzer_client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jspecify.annotations.Nullable;

@Configuration
public class McpClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are a Git repository analysis assistant. You can list recent commits
            and repository statistics using the tools available to you.
            Always use the tools to get real data rather than guessing.
            Be concise and factual in your summaries.
            """;

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            @Nullable ToolCallbackProvider mcpTools) {

        return chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(mcpTools != null ? mcpTools.getToolCallbacks() : new Object[0])
                .build();
    }
}