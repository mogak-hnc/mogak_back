package com.hnc.mogak.badge.adapter.out.persistence;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.adapter.out.persistence.repository.BadgeRepository;
import com.hnc.mogak.badge.adapter.out.persistence.repository.MemberBadgeRepository;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.badge.event.MemberBadgeCountProjection;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import com.hnc.mogak.global.util.mapper.BadgeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BadgeQueryAdapter implements BadgeQueryPort {

    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public Badge findByBadgeType(BadgeType badgeType) {
        return BadgeMapper.mapToDomainEntity(
                badgeRepository.findByBadgeType(badgeType)
                .orElseThrow(() -> new BadgeException(ErrorCode.NOT_EXISTS_BADGE)));
    }

    @Override
    public boolean hasBadge(Long memberId, Long badgeId, BadgeType badgeType) {
        return memberBadgeRepository.existsByMemberIdAndBadgeIdAndBadgeType(memberId, badgeId, badgeType);
    }

    @Override
    public List<Badge> findAll() {
        return badgeRepository.findAll().stream()
                .map(BadgeMapper::mapToDomainEntity)
                .toList();
    }

    @Override
    public List<Badge> findByBadgeByMemberId(Long memberId) {
        List<BadgeEntity> badgeEntityList = memberBadgeRepository.findBadgesByMemberId(memberId);
        return badgeEntityList.stream()
                .map(BadgeMapper::mapToDomainEntity)
                .toList();
    }

    @Override
    public List<Badge> getDurationTypeBadges() {
        return badgeRepository.findAllByBadgeType(BadgeType.DURATION)
                .stream()
                .map(BadgeMapper::mapToDomainEntity).toList();
    }

    @Override
    public List<BadgeEntity> findTopDurationBadge(BadgeType badgeType, int challengeDuration) {
        Pageable pageable = PageRequest.of(0, 1);
        return badgeRepository.findTopDurationBadge(badgeType, challengeDuration, pageable);
    }

    @Override
    public Map<Long, Integer> findBadgeCountByMemberIds(List<Long> memberIds) {
        return memberBadgeRepository.findBadgeCountByMemberIds(memberIds).stream()
                .collect(Collectors.toMap(
                                MemberBadgeCountProjection::getMemberId,
                                MemberBadgeCountProjection::getBadgeCount
                        )
                );
    }

    @Override
    public Optional<BadgeEntity> findBadgeByCountNumber(BadgeType badgeType, int badgeCount) {
        return badgeRepository.findByBadgeTypeAndConditionValue(badgeType, badgeCount);
    }
}