package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;

import java.util.List;

public interface ChallengeMemberPort {

    JoinChallengeResponse join(Member member, Challenge challenge);

    List<String> getMemberImageByChallengeId(Long challengeId, int limit);

    int getSurvivorCount(Long challengeId);

    List<Member> findMembersByChallengeId(Long challengeId);
}
