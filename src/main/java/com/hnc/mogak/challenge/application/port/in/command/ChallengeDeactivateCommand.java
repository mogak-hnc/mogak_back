package com.hnc.mogak.challenge.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeDeactivateCommand {

    private Long challengeId;
    private Long requestMemberId;
    private Long targetMemberId;
    private String role;

}
