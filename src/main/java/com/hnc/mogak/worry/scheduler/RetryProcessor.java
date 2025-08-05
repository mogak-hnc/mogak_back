package com.hnc.mogak.worry.scheduler;

import com.hnc.mogak.global.exception.exceptions.WorryException;
import com.hnc.mogak.worry.dto.CreateWorryCommentRequest;
import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.entity.RetryStatus;
import com.hnc.mogak.worry.repository.AiRetryQueueRepository;
import com.hnc.mogak.worry.service.WorryService;
import com.hnc.mogak.worry.service.ai.AiReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetryProcessor {

    private final AiReplyService aiReplyService;
    private final WorryService worryService;

    @Transactional
    public void processSingleQueue(AiRetryQueue retryQueue) {
        if (retryQueue.getRetryCount() >= 3) {
            retryQueue.updateRetryStatus(RetryStatus.FAILED);
            return;
        }

        String aiReply = aiReplyService.getAiReply(retryQueue.getTitle(), retryQueue.getBody(), retryQueue.getWorryId());
        if (aiReply == null) {
            retryQueue.increaseRetryCount();
            return;
        }

        CreateWorryCommentRequest commentRequest = new CreateWorryCommentRequest(aiReply);
        try {
            worryService.createComment(commentRequest, 2L, retryQueue.getWorryId());
            retryQueue.updateRetryStatus(RetryStatus.SUCCESS);
        } catch (WorryException e) {
            retryQueue.updateRetryStatus(RetryStatus.FAILED);
            throw e;
        }
    }

}