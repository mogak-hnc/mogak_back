package com.hnc.mogak.badge.application.port.out;

import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.member.domain.Member;

public interface BadgeCommandPort {

    void acquireBadge(Member member, Long challengeId, Badge badge);

    Long createBadge(Badge badge);

}
