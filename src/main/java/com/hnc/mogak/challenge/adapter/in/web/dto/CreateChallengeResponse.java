package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChallengeResponse {

    private Long challengeId;
    private String title;
    private String description;
    private Long creatorMemberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean official;

    public static CreateChallengeResponse build(CreateChallengeCommand command, Challenge savedChallenge, Long createMemberId) {
        return CreateChallengeResponse.builder()
                .challengeId(savedChallenge.getChallengeId().value())
                .title(savedChallenge.getContent().title())
                .description(savedChallenge.getContent().description())
                .creatorMemberId(createMemberId)
                .startDate(savedChallenge.getChallengeDuration().startDate())
                .endDate(savedChallenge.getChallengeDuration().endDate())
                .official(command.isOfficial())
                .build();
    }

}