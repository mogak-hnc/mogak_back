package com.hnc.mogak;

import com.hnc.mogak.global.cloud.S3Config;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.worry.service.ai.ChatGptCommentService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChatGptCommentServiceCircuitBreakerIntegrationTest {

    @Autowired
    ChatGptCommentService chatGptCommentService;

    @MockitoBean
    S3Service s3Service;

    @MockitoBean
    S3Config s3Config;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @AfterEach
    void cleanUp() {
        circuitBreakerRegistry.circuitBreaker("chatGpt").reset();
    }

    @Test
    void 성공과_실패율_기반_서킷상태_확인() {
        AtomicInteger callCount = new AtomicInteger(0);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenAnswer(invocation -> {
                    int n = callCount.incrementAndGet();
                    if (n <= 5) {
                        // 1~5회: 성공(Mock)
                        Map<String, Object> choice = Map.of(
                                "message", Map.of("content", "Mock Success")
                        );
                        Map<String, Object> response = Map.of(
                                "choices", List.of(choice)
                        );
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        // 6~10회: 실패
                        throw new RuntimeException("서비스 다운");
                    }
                });

        // 1) 성공 5회→ 항상 CLOSED
        for (int i = 0; i < 5; i++) {
            chatGptCommentService.getChatGptReply("제목", "본문");
            var cb = circuitBreakerRegistry.circuitBreaker("chatGpt");
            assertThat(cb.getState())
                    .as((i + 1) + "번째 성공 후에도 서킷은 계속 CLOSED")
                    .isEqualTo(CircuitBreaker.State.CLOSED);
        }

        // 2) 실패 4회→ 그래도 CLOSED (전체 9회: 5성공+4실패, 실패율 44%)
        for (int i = 0; i < 4; i++) {
            try {
                chatGptCommentService.getChatGptReply("제목", "본문");
            } catch (Exception ignored) {}
            var cb = circuitBreakerRegistry.circuitBreaker("chatGpt");
            assertThat(cb.getState())
                    .as("실패 " + (i + 1) + "회 누적(총 9회 중 4실패, 44%) 후에도 CLOSED")
                    .isEqualTo(CircuitBreaker.State.CLOSED);
        }

        // 3) 10번째(5번째 실패) → 실패율 50%, OPEN 전환!
        try {
            chatGptCommentService.getChatGptReply("제목", "본문");
        } catch (Exception ignored) {}
        var cb = circuitBreakerRegistry.circuitBreaker("chatGpt");
        assertThat(cb.getState())
                .as("총 10회 중 5회 실패(50%로 OPEN)")
                .isEqualTo(CircuitBreaker.State.OPEN);
    }

}

