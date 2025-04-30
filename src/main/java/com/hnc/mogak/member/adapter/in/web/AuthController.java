package com.hnc.mogak.member.adapter.in.web;

import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginRequest;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;
import com.hnc.mogak.member.application.port.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/auth")
@Tag(name = "2. Auth", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthUseCase authUseCase;

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 요청을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.handleSocialLogin(request.getProvider(), request.getProviderId()));
    }

}
