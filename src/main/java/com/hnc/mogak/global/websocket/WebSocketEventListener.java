package com.hnc.mogak.global.websocket;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.WebSocketException;
import com.hnc.mogak.zone.application.port.service.event.JoinMogakZoneEvent;
import com.hnc.mogak.zone.application.port.service.event.OutMogakZoneEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtUtil jwtUtil;


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        log.info("handleWebSocketConnectListener");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String token = headerAccessor.getFirstNativeHeader(AuthConstant.AUTHORIZATION);

        if (token == null || jwtUtil.isTokenExpired(token)) {
            throw new WebSocketException(ErrorCode.INVALID_TOKEN);
        }

        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        Long mogakZoneId = Long.valueOf(Objects.requireNonNull(headerAccessor.getFirstNativeHeader("mogakZoneId")));
        String sessionId = headerAccessor.getSessionId();

        sessionManager.addSession(mogakZoneId, memberId, sessionId);
    }

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        MogakZoneSessionInfo mogakZoneSessionInfo = sessionManager.removeSessionBySessionId(sessionId);
        if (mogakZoneSessionInfo != null) {
            Long mogakZoneId = mogakZoneSessionInfo.getMogakZoneId();
            Long memberId = mogakZoneSessionInfo.getMemberId();


            messagingTemplate.convertAndSend(
                    "/topic/api/mogak/zone/" + mogakZoneId,
                    new MogakZoneSessionInfo(mogakZoneId, memberId)
            );

            eventPublisher.publishEvent(
                    new OutMogakZoneEvent(
                            this,
                            mogakZoneId
                    )
            );
        }

    }

}