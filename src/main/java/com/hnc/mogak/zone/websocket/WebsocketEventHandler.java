package com.hnc.mogak.zone.websocket;

import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("웹소켓 연결 해제 로직 실행");
        Principal principal = event.getUser();

        if (principal instanceof StompPrincipal user) {
            log.info("웹소켓 principal 조건문 성공");
            Long memberId = user.getMemberId();
            Long mogakZoneId = user.getMogakZoneId();
            String sessionId = user.getSessionId();

            log.info("[웹소켓 해제 정보]memberId: {}, mogakZoneId: {}, sessionId: {}", memberId, mogakZoneId, sessionId);

            mogakZoneCommandUseCase.leave(mogakZoneId, memberId);
            messagingTemplate.convertAndSend(
                    "/topic/api/mogak/zone/" + mogakZoneId,
                    new StompPrincipal(sessionId, mogakZoneId, memberId)
            );
        } else {
            log.warn("DISCONNECT: 사용자 정보 없음 (principal is null or not StompPrincipal)");
        }

    }

}