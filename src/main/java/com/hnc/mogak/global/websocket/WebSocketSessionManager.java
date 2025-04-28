package com.hnc.mogak.global.websocket;

import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final ZoneMemberPort zoneMemberPort;
    private final Map<Long, Set<Long>> webSocketSessions = new ConcurrentHashMap<>();
    private final Map<String, MogakZoneSessionInfo> sessionIdToInfo = new ConcurrentHashMap<>();

    public void addSession(Long mogakZoneId, Long memberId, String sessionId) {
        webSocketSessions.computeIfAbsent(mogakZoneId, k -> ConcurrentHashMap.newKeySet()).add(memberId);
        sessionIdToInfo.put(sessionId, new MogakZoneSessionInfo(mogakZoneId, memberId));
    }

    public MogakZoneSessionInfo removeSessionBySessionId(String sessionId) {
        MogakZoneSessionInfo sessionInfo = sessionIdToInfo.remove(sessionId);
        if (sessionInfo != null) {
            Long mogakZoneId = sessionInfo.getMogakZoneId();
            Long memberId = sessionInfo.getMemberId();
            Set<Long> sessions = webSocketSessions.get(mogakZoneId);
            if (sessions != null) {
                sessions.remove(memberId);
                zoneMemberPort.deleteMemberByMogakZoneId(mogakZoneId, memberId);
                if (sessions.isEmpty()) {
                    webSocketSessions.remove(mogakZoneId);
                }
            }
        }
        return sessionInfo;
    }

}