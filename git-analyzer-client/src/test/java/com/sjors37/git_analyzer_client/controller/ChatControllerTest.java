package com.sjors37.git_analyzer_client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjors37.git_analyzer_client.dto.ChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ChatClient chatClient;

    @Test
    void chat_returnsReply_fromChatClient() throws Exception {
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("List the last 5 commits")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Here are the last 5 commits: ...");

        mockMvc.perform(post("/chat")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new ChatRequest("List the last 5 commits"))))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"reply": "Here are the last 5 commits: ..."}
                        """));
    }
}