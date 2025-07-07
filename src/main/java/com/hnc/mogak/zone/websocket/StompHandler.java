package com.hnc.mogak.zone.websocket;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.WebSocketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("preSend 로직 시작");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            if (Objects.requireNonNull(command) == StompCommand.CONNECT) {
                handleConnect(accessor);
            }
            if (Objects.requireNonNull(command) == StompCommand.SUBSCRIBE) {
                log.info("세션 ID: {}", accessor.getSessionId());
                log.info("구독 시작");
            }
            if (Objects.requireNonNull(command) == StompCommand.UNSUBSCRIBE) {
                log.info("세션 ID: {}", accessor.getSessionId());
                log.info("구독 종료");
            }
            if (Objects.requireNonNull(command) == StompCommand.ERROR) {
                log.info("세션 ID: {}", accessor.getSessionId());
                log.info("에러 발생");
            }

        } else {
            log.info("accessor가 null");
        }

        log.info("preSend 로직 끝");
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
//        log.info("handleConnect 로직 시작");
        log.info("세션 ID: {}", accessor.getSessionId());
        log.info("연결 시작");
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || jwtUtil.isTokenExpired(token)) {
            log.info("handle Conenction 중 토큰 예외 발생");
            throw new WebSocketException(ErrorCode.EXPIRED_TOKEN);
        }

        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String sessionId = accessor.getSessionId();
        Long mogakZoneId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("mogakZoneId")));

        accessor.setUser(new StompPrincipal(sessionId, memberId, mogakZoneId));
        log.info("CONNECT 요청 - sessionId={}, mogakZoneId={}", sessionId, mogakZoneId);
        log.info("handleConnect 로직 끝");
    }

}