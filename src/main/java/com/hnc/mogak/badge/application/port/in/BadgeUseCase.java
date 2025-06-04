package com.hnc.mogak.badge.application.port.in;

import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeRequest;
import com.hnc.mogak.badge.adapter.in.web.dto.CreateBadgeResponse;
import com.hnc.mogak.badge.adapter.in.web.dto.GetBadgeResponse;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.domain.Badge;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BadgeUseCase {

    void acquireBadge(Long memberId, Long challengeId, Badge badge);

    CreateBadgeResponse createBadge(CreateBadgeRequest request, MultipartFile imageFile);

    List<GetBadgeResponse> getBadge(Long memberId);

    List<GetBadgeResponse> getAllBadge();

}
