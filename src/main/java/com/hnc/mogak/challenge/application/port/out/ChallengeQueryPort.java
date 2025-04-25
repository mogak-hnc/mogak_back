package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.domain.challenge.Challenge;

public interface ChallengeQueryPort {

    Challenge findByChallengeId(Long challengeId);

}
