package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;

public interface ChallengeCommandPort {
    Challenge persist(Challenge challenge);

    void deleteChallenge(Challenge challenge);

    void saveChallengeBadge(Challenge challenge, Long badgeId);

    void saveChallengeOwner(Member member, Challenge challenge);

}
