package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;

public interface ChallengeCommandPort {
    Challenge persist(Member member, Challenge challenge);

}
