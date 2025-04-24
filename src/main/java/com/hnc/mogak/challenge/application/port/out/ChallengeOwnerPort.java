package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.member.domain.Member;

public interface ChallengeOwnerPort {

    Long persist(Member member, Challenge challenge);


}
