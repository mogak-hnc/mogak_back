package com.hnc.mogak.worry.service.ai;

import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.repository.AiRetryQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiReplyService {

    private final ChatGptCommentService chatGptCommentService;
    private final GeminiCommentService geminiCommentService;
    private final AiRetryQueueRepository aiRetryQueueRepository;

    public String getAiReply(String title, String body, Integer worryId) {

        String reply = null;

        try {

            reply = chatGptCommentService.getChatGptReply(title, body);

        } catch (Exception e) {
            log.error("ChatGpt Error : {}", e.getMessage());

            try {

                reply = geminiCommentService.getGeminiReply(title, body);


            } catch (Exception e1) {
                log.error("Gemini Error : {}", e1.getMessage());
                AiRetryQueue retryQueue = new AiRetryQueue(title, body, worryId);
                aiRetryQueueRepository.save(retryQueue);
            }

        }

        return reply;
    }



}
