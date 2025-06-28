package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.WebSocketUseCase;
import com.hnc.mogak.zone.application.port.in.command.ChangeStatusCommand;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MogakZoneWebSocketController {

    private final WebSocketUseCase webSocketUseCase;

    @MessageMapping("/api/mogak/zone/{mogakZoneId}") // 인식하는 url /app prefix로 붙혀야 됨.
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}") // 여기 경로 구독하고 있는 회원에게 쏨
    public SendJoinMogakZoneResponse sendJoinMogakZone(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId
    ) {
        log.info("[웹소켓] sendJoinMogakZone 시작");
        return webSocketUseCase.sendJoinMogakZone(mogakZoneId);
    }

    @MessageMapping("/api/mogak/zone/{mogakZoneId}/message")
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}/message")
    public ChatMessageResponse sendMessage(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId,
            @Payload ChatMessageRequest chatMessageRequest
    ) {
        log.info("[웹소켓] sendMessage 시작");
        SendChatMessageCommand command = SendChatMessageCommand.builder()
                .memberId(chatMessageRequest.getMemberId())
                .message(chatMessageRequest.getMessage())
                .mogakZoneId(mogakZoneId)
                .build();

        return webSocketUseCase.sendMessage(command);
    }

    @MessageMapping("/api/mogak/zone/{mogakZoneId}/status")
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}/status")
    public MogakZoneStatusResponse changeStatus(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId,
            @Payload MogakZoneStatusRequest statusRequest
    ) {
        log.info("[웹소켓] changeStatus 시작");
        ChangeStatusCommand command = ChangeStatusCommand.builder()
                .status(statusRequest.getStatus())
                .memberId(statusRequest.getMemberId())
                .mogakZoneId(mogakZoneId)
                .build();

        return webSocketUseCase.changeStatus(command);
    }

}