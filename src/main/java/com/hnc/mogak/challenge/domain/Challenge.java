package com.hnc.mogak.challenge.domain;

import com.hnc.mogak.challenge.domain.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.vo.Content;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Challenge {

    private ChallengeId challengeId;
    private Content content;
    private ChallengeDuration challengeDuration;

    public static Challenge withId(
            ChallengeId challengeId,
            Content content,
            ChallengeDuration challengeDuration) {
        return new Challenge(challengeId, content, challengeDuration);
    }

    public static Challenge withoutId(Content content, ChallengeDuration challengeDuration) {
        return new Challenge(null, content, challengeDuration);
    }

}
