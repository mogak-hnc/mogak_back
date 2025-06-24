package com.hnc.mogak.zone.websocket;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.WebSocketException;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;
//    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("preSend 로직 시작");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            if (Objects.requireNonNull(command) == StompCommand.CONNECT) {
                handleConnect(accessor);
            } else if (Objects.requireNonNull(command) == StompCommand.DISCONNECT) {
                log.info("웹소켓 연결 해제 로직 실행");
                Principal principal = accessor.getUser();

                if (principal instanceof StompPrincipal user) {
                    log.info("웹소켓 principal 조건문 성공");
                    Long memberId = user.getMemberId();
                    Long mogakZoneId = user.getMogakZoneId();
                    String sessionId = user.getSessionId();

                    log.info("[웹소켓 해제 정보]memberId: {}, mogakZoneId: {}, sessionId: {}", memberId, mogakZoneId, sessionId);

                    mogakZoneCommandUseCase.leave(mogakZoneId, memberId);
//                    messagingTemplate.convertAndSend(
//                            "/topic/api/mogak/zone/" + mogakZoneId,
//                            new StompPrincipal(sessionId, mogakZoneId, memberId)
//                    );
                } else {
                    log.warn("DISCONNECT: 사용자 정보 없음 (principal is null or not StompPrincipal)");
                }

            }
        }

        log.info("preSend 로직 끝");
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("handleConnect 로직 실행");
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || jwtUtil.isTokenExpired(token)) {
            log.info("handle Conenction 중 토큰 예외 발생");
            throw new WebSocketException(ErrorCode.EXPIRED_TOKEN);
        }

        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String sessionId = accessor.getSessionId();
        Long mogakZoneId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("mogakZoneId")));

        accessor.setUser(new StompPrincipal(sessionId, memberId, mogakZoneId));
        log.info("handleConnect 로직 끝");
    }

}