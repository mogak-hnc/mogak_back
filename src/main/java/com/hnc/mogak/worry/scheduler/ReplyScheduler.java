package com.hnc.mogak.worry.scheduler;

import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.entity.RetryStatus;
import com.hnc.mogak.worry.repository.AiRetryQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReplyScheduler {

    private final AiRetryQueueRepository retryQueueRepository;
    private final RetryProcessor retryProcessor;

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void retryReply() {
        log.info("Retry Reply Start");
        List<AiRetryQueue> retryList = retryQueueRepository.findAllByRetryStatus(RetryStatus.PENDING);

        for (AiRetryQueue retryQueue : retryList) {
            try {
                retryProcessor.processSingleQueue(retryQueue);
            } catch (Exception e) {
                log.error("Failed to process retryQueueId {}: {}", retryQueue.getId(), e.getMessage());
            }
        }
    }
}