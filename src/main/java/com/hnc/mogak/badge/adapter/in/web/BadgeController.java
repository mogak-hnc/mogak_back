package com.hnc.mogak.badge.adapter.in.web;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.in.web.dto.GetBadgeResponse;
import com.hnc.mogak.badge.application.port.in.BadgeUseCase;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
@Tag(name = "8. Badge", description = "뱃지 관련 API")
public class BadgeController {

    private final BadgeUseCase badgeUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "뱃지 생성", description = "새로운 뱃지를 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AuthConstant.ACCESS_ONLY_ADMIN)
    public ResponseEntity<CreateBadgeResponse> createBadge(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestPart("request") CreateBadgeRequest request,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        String role = jwtUtil.getRole(token);
        if (!role.equals(AuthConstant.ROLE_ADMIN)) {
            throw new BadgeException(ErrorCode.ONLY_ACCESS_ADMIN);
        }

        return ResponseEntity.ok(badgeUseCase.createBadge(request, imageFile));
    }

    @Operation(summary = "뱃지 삭제", description = "뱃지를 삭제합니다.")
    @DeleteMapping("/{badgeId}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_ADMIN)
    public ResponseEntity<Long> deleteBadge(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @PathVariable(value = "badgeId") Long badgeId
    ) {
        String role = jwtUtil.getRole(token);
        if (!role.equals(AuthConstant.ROLE_ADMIN)) {
            throw new BadgeException(ErrorCode.ONLY_ACCESS_ADMIN);
        }

        return ResponseEntity.ok(badgeUseCase.deleteBadge(badgeId));
    }

    @Operation(summary = "뱃지 전체 조회", description = "모든 뱃지를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<GetBadgeResponse>> getAllBadge() {
        return ResponseEntity.ok(badgeUseCase.getAllBadge());
    }

    @Operation(summary = "뱃지 디테일 조회", description = "뱃지를 조회합니다.")
    @GetMapping("/{badgeId}")
    public ResponseEntity<GetBadgeResponse> getBadgeDetail(@PathVariable(value = "badgeId") Long badgeId) {
        return ResponseEntity.ok(badgeUseCase.getBadgeDetail(badgeId));
    }

}