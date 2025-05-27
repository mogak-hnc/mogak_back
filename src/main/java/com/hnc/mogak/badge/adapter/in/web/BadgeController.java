package com.hnc.mogak.badge.adapter.in.web;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.application.port.in.BadgeCommandUseCase;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mogak/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeCommandUseCase badgeCommandUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "뱃지 생성", description = "새로운 뱃지를 생성합니다.")
    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_ADMIN)
    public ResponseEntity<CreateBadgeResponse> createBadge(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestBody CreateBadgeRequest request
    ) {
        String role = jwtUtil.getRole(token);
        if (!role.equals(AuthConstant.ROLE_ADMIN)) {
            throw new BadgeException(ErrorCode.ONLY_ACCESS_ADMIN);
        }

        return ResponseEntity.ok(badgeCommandUseCase.createBadge(request));
    }

}
