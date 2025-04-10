package com.hnc.mogak.member.application.port.in;

import com.hnc.mogak.member.domain.Member;

public interface LoadMemberUseCase {

    Member loadMemberByProviderId(String providerId);

}