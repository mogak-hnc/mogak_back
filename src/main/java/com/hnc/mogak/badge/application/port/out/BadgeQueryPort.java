package com.hnc.mogak.badge.application.port.out;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.domain.Badge;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BadgeQueryPort {

    Badge findByBadgeType(BadgeType badgeType);

    boolean hasBadge(Long memberId, Long badgeId, BadgeType badgeType);

    List<Badge> findAll();

    List<Badge> findByBadgeByMemberId(Long memberId);

    List<Badge> getDurationTypeBadges();

    List<BadgeEntity> findTopDurationBadge(BadgeType badgeType, int challengeDuration);

    Map<Long, Integer> findBadgeCountByMemberIds(List<Long> memberIds);

    Optional<BadgeEntity> findBadgeByCountNumber(BadgeType badgeType, int badgeCount);

    Long deleteBadge(Long badgeId);

    Badge findByBadgeId(Long badgeId);

}
