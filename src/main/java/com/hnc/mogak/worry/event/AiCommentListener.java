package com.hnc.mogak.worry.event;

import com.hnc.mogak.worry.dto.CreateWorryCommentRequest;
import com.hnc.mogak.worry.service.WorryService;
import com.hnc.mogak.worry.service.ai.AiReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AiCommentListener {

    private final AiReplyService aiReplyService;
    private final WorryService worryService;

    @Async
    @TransactionalEventListener
    public void handleWorryCreated(CreateAiCommentEvent event) {
        String aiReply = aiReplyService.getAiReply(event.getTitle(), event.getBody(), event.getWorryId());
        if (aiReply == null) return;

        CreateWorryCommentRequest commentRequest = new CreateWorryCommentRequest(aiReply);
        worryService.createComment(commentRequest, 2L, event.getWorryId());
    }

}