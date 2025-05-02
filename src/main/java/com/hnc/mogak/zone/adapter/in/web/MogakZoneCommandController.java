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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/zone")
@Tag(name = "3. MogakZone", description = "모각존 생성 및 조회 API")
public class MogakZoneCommandController {

    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "모각존 생성", description = "모각존을 새로 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateMogakZoneResponse> createMogakZone(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,

            @Parameter(
                    description = "모각존 생성 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateMogakZoneRequest.class)
                    )
            )
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

    @Operation(summary = "모각존 참여", description = "모각존에 참여합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @PostMapping("{mogakZoneId}/join")
    public ResponseEntity<JoinMogakZoneResponse> joinMogakZone(
            @Parameter(hidden = true)
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token,

            @Parameter(
                    description = "참여할 모각존 ID",
                    example = "1",
                    required = true
            )
            @PathVariable(name = "mogakZoneId") Long mogakZoneId,

            @Parameter(
                    description = "모각존 참여 정보",
                    required = false,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JoinMogakZoneRequest.class)
                    )
            )
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