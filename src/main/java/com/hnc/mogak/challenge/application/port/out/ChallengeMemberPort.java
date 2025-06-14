package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeMembersResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.query.GetChallengeMembersQuery;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ChallengeMemberPort {

    JoinChallengeResponse join(Member member, Challenge challenge);

    List<String> getMemberImageByChallengeId(Long challengeId, int limit);

    int getSurvivorCount(Long challengeId);

    List<Member> findMembersByChallengeId(Long challengeId);

    Map<Long, List<String>> getMemberImagesByChallengeIds(List<Long> challengeIds, int memberUrlLimit);

    boolean isMember(Long challengeId, Long memberId);

    Page<ChallengeMembersResponse> getChallengeMembers(GetChallengeMembersQuery query);

}
