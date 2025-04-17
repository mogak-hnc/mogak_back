package com.hnc.mogak.member.adapter.in.web;

import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginRequest;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;
import com.hnc.mogak.member.application.port.in.AuthUseCase;
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
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authUseCase.handleSocialLogin(request.getProvider(), request.getProviderId()));
    }

}
