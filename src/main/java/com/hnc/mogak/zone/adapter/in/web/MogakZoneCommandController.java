package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.util.mapper.DateParser;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneRequest;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneRequest;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/zone")
public class MogakZoneCommandController {

    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateMogakZoneResponse> createMogakZone(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestBody CreateMogakZoneRequest request) {
        String memberId = jwtUtil.getMemberId(token);

        LocalDate[] localDates = DateParser.parsePeriod(request.getPeriod());
        LocalDate startDate = localDates[0];
        LocalDate endDate = localDates[1];

        Set<String> tagNames = Arrays.stream(request.getTag().split(" "))
                .collect(Collectors.toSet());

        CreateMogakZoneCommand command = CreateMogakZoneCommand.builder()
                .name(request.getName())
                .maxCapacity(request.getMaxCapacity())
                .imageUrl(request.getImageUrl())
                .password(request.getPassword() == null ? "" : request.getPassword())
                .chatEnabled(request.isChatEnabled())
                .loginRequired(request.isLoginRequired())
                .startDate(startDate)
                .endDate(endDate)
                .memberId(Long.parseLong(memberId))
                .tagNames(tagNames)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(mogakZoneCommandUseCase.create(command));
    }

    @PostMapping("{mogakZoneId}/join")
    public ResponseEntity<JoinMogakZoneResponse> joinMogakZone(
            @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token,
            @PathVariable(name = "mogakZoneId") Long mogakZoneId,
            @Valid @RequestBody JoinMogakZoneRequest request
    ) {
        Long memberId = null;
        if (token != null) memberId = Long.parseLong(jwtUtil.getMemberId(token));

        JoinMogakZoneCommand command = JoinMogakZoneCommand.builder()
                .memberId(memberId)
                .mogakZoneId(mogakZoneId)
                .password(request.getPassword() == null ? "" : request.getPassword())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(mogakZoneCommandUseCase.join(command));
    }

}