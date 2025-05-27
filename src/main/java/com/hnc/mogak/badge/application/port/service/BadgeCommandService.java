package com.hnc.mogak.badge.application.port.service;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.application.port.in.BadgeCommandUseCase;
import com.hnc.mogak.badge.application.port.out.BadgeCommandPort;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.badge.domain.vo.BadgeId;
import com.hnc.mogak.badge.domain.vo.BadgeImage;
import com.hnc.mogak.badge.domain.vo.BadgeInfo;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeCommandService implements BadgeCommandUseCase {

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

    private Badge getBadgeDomain(CreateBadgeRequest request) {
        BadgeInfo badgeInfo = new BadgeInfo(request.getName(), request.getDescription());
        BadgeImage badgeImage = new BadgeImage(request.getIconUrl());
        BadgeType badgeType = request.getBadgeType();
        return Badge.withoutId(badgeInfo, badgeImage, badgeType);
    }

}