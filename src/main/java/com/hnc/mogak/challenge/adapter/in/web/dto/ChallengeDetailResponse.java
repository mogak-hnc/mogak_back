package com.hnc.mogak.challenge.adapter.in.web.dto;

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
    private List<String> challengeArticlesThumbnail;

    public static ChallengeDetailResponse build(List<String> memberImageList, Challenge challenge, List<String> imageThumbnailList, int survivorCount) {
        return new ChallengeDetailResponse(
                challenge.getExtraDetails().official(),
                challenge.getContent().title(),
                challenge.getChallengeDuration().startDate(),
                challenge.getChallengeDuration().endDate(),
                challenge.getExtraDetails().totalParticipants(),
                survivorCount,
                memberImageList,
                imageThumbnailList
        );
    }

}