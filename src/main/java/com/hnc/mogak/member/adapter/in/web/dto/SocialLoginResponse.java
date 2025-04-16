package com.hnc.mogak.member.adapter.in.web.dto;

import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SocialLoginResponse {

    private Long memberId;
    private String token;

}
