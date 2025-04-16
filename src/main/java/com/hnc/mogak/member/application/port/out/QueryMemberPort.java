package com.hnc.mogak.member.application.port.out;

import com.hnc.mogak.member.domain.Member;

public interface QueryMemberPort {

    Member loadMemberByProviderId(String providerId);

    Member loadMemberByMemberId(Long memberId);
    boolean existsByProviderId(String providerId);
    boolean existsByNickname(String nickname);

}
