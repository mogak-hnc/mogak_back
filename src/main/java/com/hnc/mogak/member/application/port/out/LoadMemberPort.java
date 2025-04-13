package com.hnc.mogak.member.application.port.out;

import com.hnc.mogak.member.domain.Member;

import java.util.Optional;

public interface LoadMemberPort {

    Member loadMemberByProviderId(String providerId);
    boolean existsByProviderId(String providerId);
    boolean existsByNickname(String nickname);

}
