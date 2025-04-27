package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.GetMogakZoneDetailQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MogakZoneWebSocketController {

    private final JwtUtil jwtUtil;
    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

    @MessageMapping("/api/mogak/zone/{mogakZoneId}") // 인식하는 url /app prefix로 붙혀야 됨.
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}") // 여기 경로 구독하고 있는 회원에게 쏨
    public MogakZoneDetailResponse getMogakZoneDetail(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId,
            @Payload String token
    ) {
        String memberId = jwtUtil.getMemberId(token);
        String nickname = jwtUtil.getNickname(token);

        GetMogakZoneDetailQuery detailQuery = GetMogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .nickname(nickname)
                .memberId(Long.parseLong(memberId))
                .build();

        return mogakZoneQueryUseCase.getDetail(detailQuery);
    }

}