package com.hnc.mogak.badge.application.port.in;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;

public interface BadgeCommandUseCase {

    void acquireBadge(Long userId, Long challengeId, BadgeType badgeType);

    CreateBadgeResponse createBadge(CreateBadgeRequest request);

}
