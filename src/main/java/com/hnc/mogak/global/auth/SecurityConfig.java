package com.hnc.mogak.global.auth;

import com.hnc.mogak.global.auth.jwt.JwtFilter;
import com.hnc.mogak.global.auth.oauth.CustomAuthenticationFailureHandler;
import com.hnc.mogak.global.auth.oauth.CustomAuthenticationSuccessHandler;
import com.hnc.mogak.global.auth.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomAuthenticationSuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationFailureHandler oAuth2FailureHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
                // error endpoint를 열어줘야 함, favicon.ico 추가!
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // rest api 설정
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
                .headers(c -> c.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

                // request 인증, 인가 설정
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        "/",
                                        "/login",
                                        "/api/mogak/auth/social-login",
                                        "/api/mogak",
                                        "/api/mogak/zone",
                                        "/api/mogak/zone/join/*",
                                        "/api/mogak/zone/list",
                                        "/api/mogak/zone/tags",
                                        "/api/mogak/zone/*/detail",
                                        "/api/mogak/challenge",
                                        "/api/mogak/challenge/*",
                                        "/api/mogak/challenge/*/verification",
                                        "/api/mogak/challenge/list",
                                        "/api/mogak/worry",
                                        "/api/mogak/worry/*",
                                        "/api/mogak/worry/*/empathy",
                                        "/ws",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/swagger-ui.html",
                                        "/dashboard/**",
                                        "/webjars/**"
                                ).permitAll()
                                .anyRequest().authenticated())

                // oauth2 설정
//                .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
//                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
//                        oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
//                                // 로그인 성공 시 핸들러
//                                .successHandler(oAuth2SuccessHandler)
//                                .failureHandler(oAuth2FailureHandler)
//                )

                // jwt 관련 설정
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

////                 인증 예외 핸들링(프론트가 페이지 만들면 활성화)
//                .exceptionHandling((exceptions) -> exceptions
//                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
