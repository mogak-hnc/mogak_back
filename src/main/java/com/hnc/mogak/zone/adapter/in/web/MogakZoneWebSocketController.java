package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageRequest;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MogakZoneWebSocketController {

    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;
    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;

    @MessageMapping("/api/mogak/zone/{mogakZoneId}") // 인식하는 url /app prefix로 붙혀야 됨.
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}") // 여기 경로 구독하고 있는 회원에게 쏨
    public MogakZoneDetailResponse getMogakZoneDetail(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId
    ) {
        MogakZoneDetailQuery detailQuery = MogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .build();

        return mogakZoneQueryUseCase.getDetail(detailQuery);
    }

    @MessageMapping("/api/mogak/zone/{mogakZoneId}/message")
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}")
    public ChatMessageResponse sendMessage(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId,
            @Payload ChatMessageRequest chatMessageRequest
    ) {
        SendChatMessageCommand command = SendChatMessageCommand.builder()
                .memberId(chatMessageRequest.getMemberId())
                .message(chatMessageRequest.getMessage())
                .mogakZoneId(mogakZoneId)
                .build();

        return mogakZoneCommandUseCase.sendMessage(command);
    }

}