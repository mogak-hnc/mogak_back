package com.hnc.mogak;

import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.entity.RetryStatus;
import com.hnc.mogak.worry.scheduler.RetryProcessor;
import com.hnc.mogak.worry.service.WorryService;
import com.hnc.mogak.worry.service.ai.AiReplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class RetryProcessorTest {

    private AiReplyService aiReplyService;
    private WorryService worryService;
    private RetryProcessor retryProcessor;

    @BeforeEach
    void setUp() {
        aiReplyService = mock(AiReplyService.class);
        worryService = mock(WorryService.class);
        retryProcessor = new RetryProcessor(aiReplyService, worryService);
    }

    @Test
    void retryCount_over_3_convert_to_FAILED() {
        AiRetryQueue queue = new AiRetryQueue("제목", "본문", 1);
        queue.increaseRetryCount();
        queue.increaseRetryCount();
        queue.increaseRetryCount();

        retryProcessor.processSingleQueue(queue);

        assertThat(queue.getRetryCount()).isEqualTo(3);
        assertThat(queue.getRetryStatus()).isEqualTo(RetryStatus.FAILED);
    }

    @Test
    void increase_retryCount_when_response_is_null() {
        AiRetryQueue queue = new AiRetryQueue("제목", "본문", 1);
        when(aiReplyService.getAiReply(anyString(), anyString(), anyInt())).thenReturn(null);

        retryProcessor.processSingleQueue(queue);

        assert (queue.getRetryCount() == 1);
    }

    @Test
    void success_response() {
        AiRetryQueue queue = new AiRetryQueue("제목", "본문", 1);
        when(aiReplyService.getAiReply(anyString(), anyString(), anyInt())).thenReturn("응답");

        retryProcessor.processSingleQueue(queue);

        verify(worryService, times(1)).createComment(any(), eq(2L), eq(1));
        assertThat(queue.getRetryStatus()).isEqualTo(RetryStatus.SUCCESS);
    }

}