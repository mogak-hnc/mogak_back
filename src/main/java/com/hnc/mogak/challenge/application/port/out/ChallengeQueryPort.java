package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.domain.challenge.Challenge;

import java.util.List;

public interface ChallengeQueryPort {

    Challenge findByChallengeId(Long challengeId);

    List<Challenge> findTopChallengesByParticipants(int limit);

}
