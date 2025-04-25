package com.hnc.mogak.challenge.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JoinChallengeCommand {

    private Long memberId;
    private Long challengeId;

}
