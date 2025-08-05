package com.hnc.mogak.worry.service.ai;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiCommentService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    @CircuitBreaker(name = "gemini", fallbackMethod = "fallbackGemini")
    public String getGeminiReply(String title, String body) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("고민 제목: %s\n고민 내용: %s\n이 고민에 공감하고 힘이 되는 짧은 말을 50자 이내로 해줘.", title, body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        String url = BASE_URL + MODEL_NAME + ":generateContent?key=" + apiKey;

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

        return (String) parts.get(0).get("text");
    }

    public String fallbackGemini(String title, String body, Throwable t) {
        log.warn("Fallback for Gemini called due to: {}", t.getMessage());
        throw new RuntimeException("Gemini service unavailable", t);
    }

}