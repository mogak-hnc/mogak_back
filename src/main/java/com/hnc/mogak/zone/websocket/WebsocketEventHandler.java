package com.hnc.mogak.zone.websocket;

import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.SendJoinMogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventHandler {

    private final ZoneMemberPort zoneMemberPort;
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

            mogakZoneCommandUseCase.offline(mogakZoneId, memberId);
            List<ZoneMember> zoneMemberList =  zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZoneId);
            SendJoinMogakZoneResponse sendJoinMogakZoneResponse = ZoneMemberMapper.mapToSendJoinMogakZoneResponse(zoneMemberList);
            messagingTemplate.convertAndSend(
                    "/topic/api/mogak/zone/" + mogakZoneId,
                    sendJoinMogakZoneResponse
            );
        } else {
            log.warn("DISCONNECT: 사용자 정보 없음 (principal is null or not StompPrincipal)");
        }

    }

}