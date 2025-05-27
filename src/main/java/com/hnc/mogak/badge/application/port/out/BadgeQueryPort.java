package com.hnc.mogak.badge.application.port.out;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.member.domain.Member;

import java.util.List;

public interface BadgeQueryPort {

    Badge findByBadgeType(BadgeType badgeType);

    boolean hasBadge(Long memberId, BadgeType badgeType);

    List<Badge> findAll();

    List<BadgeEntity> findByBadgeByMemberId(Long memberId);

}
