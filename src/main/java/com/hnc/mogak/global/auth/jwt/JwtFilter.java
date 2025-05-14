package com.hnc.mogak.global.auth.jwt;

import com.hnc.mogak.global.redis.RedisConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.hnc.mogak.global.auth.AuthConstant.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isTokenExpired(accessToken) || isBlackList(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }



        String findId = jwtUtil.getMemberId(accessToken);
        String findRole = jwtUtil.getRole(accessToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(findId, findRole);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private boolean isBlackList(String accessToken) {
        return redisTemplate.opsForValue().get(RedisConstant.BLACK_LIST + accessToken) != null;
    }

}
