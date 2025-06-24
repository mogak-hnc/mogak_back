package com.hnc.mogak.zone.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.DelegateHostCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.UpdateMogakZoneCommand;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zone")
@Tag(name = "3. MogakZone", description = "모각존 생성 및 조회 API")
public class MogakZoneCommandController {

    private final MogakZoneCommandUseCase mogakZoneCommandUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "모각존 생성", description = "모각존을 새로 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            @Valid @RequestPart CreateMogakZoneRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String memberId = jwtUtil.getMemberId(token);

        Set<String> tagNames = Arrays.stream(request.getTag().split(" "))
                .collect(Collectors.toSet());

        String pw = request.isPasswordRequired() ? request.getPassword() : "";
        if (pw == null) throw new MogakZoneException(ErrorCode.NEED_PASSWORD);

        CreateMogakZoneCommand command = CreateMogakZoneCommand.builder()
                .name(request.getName())
                .maxCapacity(request.getMaxCapacity())
                .imageUrl(image)
                .password(pw)
                .chatEnabled(request.isChatEnabled())
                .memberId(Long.parseLong(memberId))
                .tagNames(tagNames)
                .passwordRequired(request.isPasswordRequired())
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

    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @DeleteMapping("/{mogakZoneId}")
    public ResponseEntity<Long> deleteMogakZone(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "삭제할 모각존 ID")
            @PathVariable(name = "mogakZoneId") Long mogakZoneId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneCommandUseCase.deleteMogakZone(mogakZoneId, memberId, role));
    }

    @DeleteMapping("/leave")
    public ResponseEntity<ExitMogakZoneRequest> leaveMogakZone(@Valid @RequestBody ExitMogakZoneRequest request) {
        mogakZoneCommandUseCase.leave(request.getMogakZoneId(), request.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(request);
    }

    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @PostMapping("/{mogakZoneId}/kick")
    public ResponseEntity<Long> kickMember(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "참여자를 제거할 모각존 ID")
            @PathVariable(name = "mogakZoneId") Long mogakZoneId,
            @Parameter(description = "참여자 ID")
            @RequestParam(name = "targetMemberId") Long targetMemberId
    ) {
        Long ownerMemberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneCommandUseCase.kickMember(mogakZoneId, ownerMemberId, targetMemberId, role));
    }

    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @PutMapping("/{mogakZoneId}")
    public ResponseEntity<Void> updateMogakZone(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "모각존 업데이트 정보")
            @PathVariable(name = "mogakZoneId") Long mogakZoneId,
            @Parameter(description = "모각존 업데이트 정보")
            @RequestParam(value = "image") MultipartFile imageUrl
    ) {
        Long requestMemberId = Long.parseLong(jwtUtil.getMemberId(token));

        UpdateMogakZoneCommand command = UpdateMogakZoneCommand.builder()
                .imageUrl(imageUrl)
                .mogakZoneId(mogakZoneId)
                .requestMemberId(requestMemberId)
                .build();

        mogakZoneCommandUseCase.updateMogakZone(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{mogakZoneId}/delegate-host")
    public ResponseEntity<Void> delegateHost(@RequestHeader("Authorization") String token,
                                             @PathVariable Long mogakZoneId,
                                             @RequestBody DelegateHostRequest request) {
        Long currentHostId = Long.parseLong(jwtUtil.getMemberId(token));

        DelegateHostCommand command = new DelegateHostCommand(mogakZoneId, currentHostId, request.getNewHostId());
        mogakZoneCommandUseCase.delegateHost(command);
        return ResponseEntity.ok().build();
    }


}