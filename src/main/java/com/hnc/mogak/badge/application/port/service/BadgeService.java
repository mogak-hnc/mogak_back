package com.hnc.mogak.badge.application.port.service;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.in.web.dto.GetBadgeResponse;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.application.port.in.BadgeUseCase;
import com.hnc.mogak.badge.application.port.out.BadgeCommandPort;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.badge.domain.vo.BadgeImage;
import com.hnc.mogak.badge.domain.vo.BadgeInfo;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService implements BadgeUseCase {

    private final MemberPort memberPort;
    private final BadgeQueryPort badgeQueryPort;
    private final BadgeCommandPort badgeCommandPort;

    @Override
    public void acquireBadge(Long memberId, Long challengeId, BadgeType badgeType) {
        Member member = memberPort.loadMemberByMemberId(memberId);
        Badge badge = badgeQueryPort.findByBadgeType(badgeType);

        if (badgeQueryPort.hasBadge(memberId, badgeType)) return;

        badgeCommandPort.acquireBadge(member, challengeId, badge);
    }

    @Override
    public CreateBadgeResponse createBadge(CreateBadgeRequest request) {
        return new CreateBadgeResponse(
                badgeCommandPort.createBadge(getBadgeDomain(request)));
    }

    @Override
    public List<GetBadgeResponse> getBadge(Long memberId) {
        List<Badge> badgeList = badgeQueryPort.findByBadgeByMemberId(memberId);

        return badgeList.stream().map(badge -> new GetBadgeResponse(
                        badge.getBadgeId().value(),
                        badge.getBadgeType().name(),
                        badge.getBadgeInfo().description(),
                        badge.getBadgeImage().iconUrl(),
                        badge.getBadgeType()
                ))
                .toList();
    }

    private Badge getBadgeDomain(CreateBadgeRequest request) {
        BadgeInfo badgeInfo = new BadgeInfo(request.getName(), request.getDescription());
        BadgeImage badgeImage = new BadgeImage(request.getIconUrl());
        BadgeType badgeType = request.getBadgeType();
        return Badge.withoutId(badgeInfo, badgeImage, badgeType);
    }

}