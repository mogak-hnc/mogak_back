package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeMemberDeactivateResponse {

    private Long challengeId;
    private Long kickedMemberId;

}
