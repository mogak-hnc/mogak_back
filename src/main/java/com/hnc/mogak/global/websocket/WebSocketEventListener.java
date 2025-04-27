package com.hnc.mogak.global.websocket;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketEventListener extends AbstractWebSocketHandler {

    private final Map<Long, Map<String, WebSocketSession>> zoneMembers = new ConcurrentHashMap<>();


}
