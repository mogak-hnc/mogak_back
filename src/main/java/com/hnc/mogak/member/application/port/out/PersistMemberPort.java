package com.hnc.mogak.member.application.port.out;

import com.hnc.mogak.member.domain.Member;

public interface PersistMemberPort {

    Long persistMember(Member member);

}
