package com.hnc.mogak.zone.websocket;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.AuthException;
import com.hnc.mogak.global.exception.exceptions.WebSocketException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            if (Objects.requireNonNull(command) == StompCommand.CONNECT) {
                handleConnect(accessor);
            }
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || jwtUtil.isTokenExpired(token)) {
            throw new WebSocketException(ErrorCode.EXPIRED_TOKEN);
        }

        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String sessionId = accessor.getSessionId();
        Long mogakZoneId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("mogakZoneId")));

        accessor.setUser(new StompPrincipal(sessionId, memberId, mogakZoneId));
    }

}