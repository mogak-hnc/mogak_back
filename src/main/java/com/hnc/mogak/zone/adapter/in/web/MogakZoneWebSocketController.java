package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.GetMogakZoneDetailQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MogakZoneWebSocketController {

    private final JwtUtil jwtUtil;
    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

//    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @MessageMapping("/api/mogak/zone/{mogakZoneId}") // 인식하는 url /app prefix로 붙혀야 됨.
    @SendTo("/topic/api/mogak/zone/{mogakZoneId}") // 여기 경로 구독하고 있는 회원에게 쏨
    public MogakZoneDetailResponse getMogakZoneDetail(
            @DestinationVariable(value = "mogakZoneId") Long mogakZoneId
    ) {
        GetMogakZoneDetailQuery detailQuery = GetMogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .build();

        return mogakZoneQueryUseCase.getDetail(detailQuery);
    }

}