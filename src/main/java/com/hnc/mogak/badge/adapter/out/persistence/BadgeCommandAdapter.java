package com.hnc.mogak.badge.adapter.out.persistence;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.MemberBadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.repository.BadgeRepository;
import com.hnc.mogak.badge.adapter.out.persistence.repository.MemberBadgeRepository;
import com.hnc.mogak.badge.application.port.out.BadgeCommandPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.global.util.mapper.BadgeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BadgeCommandAdapter implements BadgeCommandPort {

    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    @Override
    public void acquireBadge(Member member, Long challengeId, Badge badge) {
        MemberEntity memberEntity = MemberMapper.mapToJpaEntity(member);
        BadgeEntity badgeEntity = BadgeMapper.mapToJpaEntity(badge);

        memberBadgeRepository.save(MemberBadgeEntity.builder()
                .memberEntity(memberEntity)
                .badgeEntity(badgeEntity)
                .challengeId(challengeId)
                .build());
    }

    @Override
    public Long createBadge(Badge badge) {
        BadgeEntity badgeEntity = badgeRepository.save(BadgeMapper.mapToJpaEntity(badge));
        return badgeEntity.getId();
    }
}
