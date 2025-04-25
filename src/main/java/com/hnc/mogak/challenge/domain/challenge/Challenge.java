package com.hnc.mogak.challenge.domain.challenge;

import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.challenge.vo.Content;
import com.hnc.mogak.challenge.domain.challenge.vo.ExtraDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Challenge {

    private ChallengeId challengeId;
    private Content content;
    private ExtraDetails extraDetails;
    private ChallengeDuration challengeDuration;

    public static Challenge withId(
            ChallengeId challengeId,
            Content content,
            ExtraDetails extraDetails,
            ChallengeDuration challengeDuration) {
        return new Challenge(challengeId, content, extraDetails, challengeDuration);
    }

    public static Challenge withoutId(Content content, ExtraDetails extraDetails, ChallengeDuration challengeDuration) {
        return new Challenge(null, content, extraDetails, challengeDuration);
    }

}
