package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetChallengeArticleThumbNail {

    private Long challengeArticleId;
    private Long memberId;
    private String thumbnailUrl;

}
