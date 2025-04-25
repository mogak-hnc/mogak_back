package com.hnc.mogak.challenge.domain.challengemember;

import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;
import lombok.Getter;

@Getter
public class ChallengeMember {

    private ChallengeMemberId challengeMemberId;
    private Member member;
    private Challenge challenge;

}
