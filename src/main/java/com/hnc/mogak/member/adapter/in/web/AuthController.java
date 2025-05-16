package com.hnc.mogak.member.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginRequest;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import com.hnc.mogak.member.application.port.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/auth")
@Tag(name = "2. Auth", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 요청을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.handleSocialLogin(request.getProvider(), request.getProviderId()));
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 반환합니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER)
    @GetMapping
    public ResponseEntity<MemberInfoResponse> getMemberInfo(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.OK).body(authUseCase.getMemberInfo(memberId));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자를 탈퇴시킵니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER)
    @DeleteMapping
    public ResponseEntity<Long> deleteMember (
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.OK).body(authUseCase.deleteMember(memberId, token));
    }

    @Operation(summary = "회원 수정", description = "로그인한 사용자의 정보를 수정합니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateMemberInfoResponse> updateMember(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage,
            @RequestParam(value = "showBadge", required = false) boolean showBadge
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.ok(authUseCase.updateMemberInfo(memberId, nickname, file, deleteImage, showBadge));
    }

}
