package com.hnc.mogak.challenge.application.port.in.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetChallengeMembersQuery {

    private Long challengeId;
    private int page;
    private int size;

}
