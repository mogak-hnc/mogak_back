package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeDetailResponse {

    private boolean official;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalParticipants;
    private int survivorCount;
    private List<String> memberImageList;
    private boolean isJoined;
    private Long challengeOwnerId;
    private ChallengeStatus status;

    public static ChallengeDetailResponse build(
            List<String> memberImageList,
            Challenge challenge,
            int survivorCount,
            boolean isJoined,
            Long challengeOwnerId) {

        return new ChallengeDetailResponse(
                challenge.getExtraDetails().official(),
                challenge.getContent().title(),
                challenge.getChallengeDuration().startDate(),
                challenge.getChallengeDuration().endDate(),
                challenge.getExtraDetails().totalParticipants(),
                survivorCount,
                memberImageList,
                isJoined,
                challengeOwnerId,
                challenge.getStatus()
        );
    }

}