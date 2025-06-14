package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeMembersResponse {

    private Long memberId;
    private String nickname;
    private String memberImageUrl;
    private boolean isSurvivor;

}
