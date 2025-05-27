package com.hnc.mogak.badge.adapter.out.persistence;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.adapter.out.persistence.repository.BadgeRepository;
import com.hnc.mogak.badge.adapter.out.persistence.repository.MemberBadgeRepository;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import com.hnc.mogak.global.util.mapper.BadgeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public boolean hasBadge(Long memberId, BadgeType badgeType) {
        return memberBadgeRepository.existsByMemberIdAndBadgeType(memberId, badgeType);
    }

    @Override
    public List<Badge> findAll() {
        return badgeRepository.findAll().stream()
                .map(BadgeMapper::mapToDomainEntity)
                .toList();
    }

    @Override
    public List<BadgeEntity> findByBadgeByMemberId(Long memberId) {
        return memberBadgeRepository.findBadgesByMemberId(memberId);
    }

}
