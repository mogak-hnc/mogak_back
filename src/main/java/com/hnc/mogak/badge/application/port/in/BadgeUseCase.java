package com.hnc.mogak.badge.application.port.in;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.in.web.dto.GetBadgeResponse;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.domain.Badge;

import java.util.List;

public interface BadgeUseCase {

    void acquireBadge(Long userId, Long challengeId, BadgeType badgeType);

    CreateBadgeResponse createBadge(CreateBadgeRequest request);

    List<GetBadgeResponse> getBadge(Long memberId);

}
