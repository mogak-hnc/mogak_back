package com.hnc.mogak.worry.service.ai;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatGptCommentService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @CircuitBreaker(name = "chatGpt", fallbackMethod = "fallbackChatGpt")
    public String getChatGptReply(String title, String body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = getMapHttpEntity(title, body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }

    private HttpEntity<Map<String, Object>> getMapHttpEntity(String title, String body, HttpHeaders headers) {
        String prompt = String.format(
                "고민 제목: %s\n고민 내용: %s\n이 고민에 공감하고 힘이 되는 짧은 말을 해줘.",
                title, body
        );

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "gpt-3.5-turbo");
        bodyMap.put("temperature", 0.8);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "너는 따뜻하고 공감 능력이 뛰어난 조언봇이야."),
                Map.of("role", "user", "content", prompt)
        );
        bodyMap.put("messages", messages);

        return new HttpEntity<>(bodyMap, headers);
    }

    public String fallbackChatGpt(String title, String body, Throwable t) {
        log.warn("Fallback for ChatGPT called due to: {}", t.getMessage());
        throw new RuntimeException("ChatGPT service unavailable", t);
    }

}
