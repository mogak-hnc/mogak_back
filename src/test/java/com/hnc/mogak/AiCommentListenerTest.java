package com.hnc.mogak;

import com.hnc.mogak.worry.dto.CreateWorryCommentRequest;
import com.hnc.mogak.worry.event.AiCommentListener;
import com.hnc.mogak.worry.event.CreateAiCommentEvent;
import com.hnc.mogak.worry.service.WorryService;
import com.hnc.mogak.worry.service.ai.AiReplyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class AiCommentListenerTest {

    @Mock
    private AiReplyService aiReplyService;
    @Mock
    private WorryService worryService;

    @InjectMocks
    private AiCommentListener aiCommentListener;

    public AiCommentListenerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("AI 응답이 정상적으로 생성되면 worryService.createComment가 호출된다")
    void testA() {
        // given
        CreateAiCommentEvent event = new CreateAiCommentEvent("제목", "본문", 1);
        when(aiReplyService.getAiReply(anyString(), anyString(), anyInt())).thenReturn("AI 응답");

        // when
        aiCommentListener.handleWorryCreated(event);

        // then
        ArgumentCaptor<CreateWorryCommentRequest> captor = ArgumentCaptor.forClass(CreateWorryCommentRequest.class);
        verify(worryService, times(1)).createComment(captor.capture(), eq(2L), eq(1));
        assertThat(captor.getValue().getComment()).isEqualTo("AI 응답");
    }


    @Test
    @DisplayName("AI 응답이 null이면 worryService.createComment가 호출되지 않는다")
    void testB() {
        // given
        CreateAiCommentEvent event = new CreateAiCommentEvent("제목", "본문", 1);
        when(aiReplyService.getAiReply(anyString(), anyString(), anyInt())).thenReturn(null);

        // when
        aiCommentListener.handleWorryCreated(event);

        // then
        verify(worryService, never()).createComment(any(), anyLong(), anyInt());
    }

}