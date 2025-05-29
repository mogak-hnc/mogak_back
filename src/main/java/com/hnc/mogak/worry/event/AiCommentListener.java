package com.hnc.mogak.worry.event;

import com.hnc.mogak.worry.dto.CreateWorryCommentRequest;
import com.hnc.mogak.worry.service.AiCommentService;
import com.hnc.mogak.worry.service.WorryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AiCommentListener {

    private final AiCommentService aiCommentService;
    private final WorryService worryService;

    @Async
    @TransactionalEventListener
    public void handleWorryCreated(CreateAiCommentEvent event) {
        String aiReply = aiCommentService.getAiReply(event.getTitle(), event.getBody());
        CreateWorryCommentRequest commentRequest = new CreateWorryCommentRequest(aiReply);
        worryService.createComment(commentRequest, 2L, event.getWorryId());
    }

}