package com.hnc.mogak.member.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SocialLoginRequest {

    @Schema(description = "소셜 로그인 제공자 (예: kakao, naver)", example = "kakao")
    private String provider;

    @Schema(description = "소셜 제공자의 사용자 ID", example = "1234")
    private String providerId;

}
