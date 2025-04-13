package com.hnc.mogak.global.auth.oauth;

import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${spring.jwt.access-token.expiration-time}")
    private Long expiredMs;
    private final JwtUtil jwtUtil;

    private static final String URI = "/auth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.member();

        Long memberId = member.getMemberId().value();
        String nickname = member.getMemberInfo().nickname();
        String role = member.getRole().value();

        String accessToken = jwtUtil.generateToken(memberId, nickname, role, expiredMs);

        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

}
