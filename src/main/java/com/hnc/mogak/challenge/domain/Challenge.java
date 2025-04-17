package com.hnc.mogak.challenge.domain;

import com.hnc.mogak.challenge.domain.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.vo.Content;
import com.hnc.mogak.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Challenge {

    private ChallengeId challengeId;
    private Content content;
    private ChallengeDuration challengeDuration;
    private Member challengeCreator;


    public static Challenge withId(
            ChallengeId challengeId,
            Content content,
            ChallengeDuration challengeDuration,
            Member challengeCreator) {
        return new Challenge(challengeId, content, challengeDuration, challengeCreator);
    }

}
