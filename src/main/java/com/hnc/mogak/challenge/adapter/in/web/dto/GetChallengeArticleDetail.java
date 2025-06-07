package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetChallengeArticleDetail {

    private Long challengeArticleId;
    private String description;
    private Long challengeId;

    private Long memberId;
    private String nickname;

    private List<String> imageUrl;

    private LocalDateTime createdAt;
}
