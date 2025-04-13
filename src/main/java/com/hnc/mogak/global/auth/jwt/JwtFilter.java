package com.hnc.mogak.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.hnc.mogak.global.auth.AuthConstant.AUTHORIZATION;
import static com.hnc.mogak.global.auth.AuthConstant.BEARER;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(BEARER.length()).trim();
        if (jwtUtil.isTokenExpired(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String findProviderId = jwtUtil.getProviderId(accessToken);
        String findRole = jwtUtil.getRole(accessToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(findProviderId, findRole);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
