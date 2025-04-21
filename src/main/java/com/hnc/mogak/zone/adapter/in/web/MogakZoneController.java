package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.util.mapper.DateParser;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneRequest;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
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
public class MogakZoneController {

    private final MogakZoneUseCase mogakZoneUseCase;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<MogakZoneResponse> createMogakZone(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestBody MogakZoneRequest request) {
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
                .password(request.getPassword())
                .chatEnabled(request.isChatEnabled())
                .loginRequired(request.isLoginRequired())
                .startDate(startDate)
                .endDate(endDate)
                .memberId(Long.parseLong(memberId))
                .tagNames(tagNames)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(mogakZoneUseCase.create(command));
    }

}