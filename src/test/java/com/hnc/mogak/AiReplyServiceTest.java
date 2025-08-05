package com.hnc.mogak;

import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.repository.AiRetryQueueRepository;
import com.hnc.mogak.worry.service.ai.AiReplyService;
import com.hnc.mogak.worry.service.ai.ChatGptCommentService;
import com.hnc.mogak.worry.service.ai.GeminiCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiReplyServiceTest {

    private ChatGptCommentService chatGptCommentService;
    private GeminiCommentService geminiCommentService;
    private AiRetryQueueRepository aiRetryQueueRepository;
    private AiReplyService aiReplyService;

    @BeforeEach
    void setUp() {
        chatGptCommentService = mock(ChatGptCommentService.class);
        geminiCommentService = mock(GeminiCommentService.class);
        aiRetryQueueRepository = mock(AiRetryQueueRepository.class);
        aiReplyService = new AiReplyService(chatGptCommentService, geminiCommentService, aiRetryQueueRepository);
    }

    @Test
    @DisplayName("챗지피티 서비스가 정상적으로 호출된다")
    void testA() {
        //given
        when(chatGptCommentService.getChatGptReply(anyString(), anyString())).thenReturn("chatgpt 응답");

        // when
        String reply = aiReplyService.getAiReply("title", "body", 1);

        // then
        assertThat(reply).isEqualTo("chatgpt 응답");
        verify(geminiCommentService, never()).getGeminiReply(any(), any());
        verify(aiRetryQueueRepository, never()).save(any());
    }

    @Test
    @DisplayName("챗지피티에서 에러가 발생할 경우, gemini 서비스가 호출된다")
    void testC() {
        // given
        when(chatGptCommentService.getChatGptReply(anyString(), anyString()))
                .thenThrow(new RuntimeException("gpt error"));
        when(geminiCommentService.getGeminiReply(anyString(), anyString()))
                .thenReturn("gemini 응답");

        // when
        String reply = aiReplyService.getAiReply("title", "body", 2);

        // then
        assertThat(reply).isEqualTo("gemini 응답");
        verify(aiRetryQueueRepository, never()).save(any());
    }

    @Test
    @DisplayName("챗지피티, 제미나이 예외 시, 큐에 저장")
    void testD() {
        // given
        when(chatGptCommentService.getChatGptReply(anyString(), anyString()))
                .thenThrow(new RuntimeException("gpt error"));
        when(geminiCommentService.getGeminiReply(anyString(), anyString()))
                .thenThrow(new RuntimeException("gemini error"));

        // when
        String reply = aiReplyService.getAiReply("title", "body", 3);

        // then
        assertThat(reply).isNull();
        verify(aiRetryQueueRepository, times(1)).save(any(AiRetryQueue.class));
    }

}
