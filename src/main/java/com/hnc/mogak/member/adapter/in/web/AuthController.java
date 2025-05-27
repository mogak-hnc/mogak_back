package com.hnc.mogak.member.adapter.in.web;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.member.adapter.in.web.dto.*;
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

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 요청을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<LoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.handleSocialLogin(request.getProvider(), request.getProviderId()));
    }

    @Operation(summary = "운영자 로그인", description = "운영자 로그인 요청을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/admin-login")
    public ResponseEntity<LoginResponse> loginAdmin(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.loginAdmin(request.getId(), request.getPw()));
    }

}