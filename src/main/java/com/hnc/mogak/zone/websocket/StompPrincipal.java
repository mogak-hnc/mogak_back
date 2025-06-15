package com.hnc.mogak.zone.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class StompPrincipal implements Principal {

    private final String sessionId;
    private final Long memberId;
    private final Long mogakZoneId;

    @Override
    public String getName() {
        return sessionId;
    }

}