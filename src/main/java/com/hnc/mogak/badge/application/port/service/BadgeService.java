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
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.monitoring.RequestContextHolder;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService implements BadgeUseCase {

    private final MemberPort memberPort;
    private final BadgeQueryPort badgeQueryPort;
    private final BadgeCommandPort badgeCommandPort;

    private final S3Service s3Service;

    @Override
    public void acquireBadge(Long memberId, Long challengeId, Badge badge) {
        log.info("[{}] [뱃지 획득 로직 실행]", RequestContextHolder.getContext().getUuid());
        if (badgeQueryPort.hasBadge(memberId, badge.getBadgeId().value(), badge.getBadgeType())) {
            return;
        }

        Member member = memberPort.loadMemberByMemberId(memberId);
        badgeCommandPort.acquireBadge(member, challengeId, badge);
    }

    @Override
    public CreateBadgeResponse createBadge(CreateBadgeRequest request, MultipartFile imageFile) {
        String iconUrl = s3Service.uploadImage(imageFile, "badge");
        return new CreateBadgeResponse(badgeCommandPort.createBadge(getBadgeDomain(request, iconUrl)));
    }

    @Override
    public Long deleteBadge(Long badgeId) {
        return badgeQueryPort.deleteBadge(badgeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetBadgeResponse> getMemberBadge(Long requestMemberId, Long targetMemberId) {
        Member targetMember = memberPort.loadMemberByMemberId(targetMemberId);

        if (!targetMember.isShowBadge() && !Objects.equals(requestMemberId, targetMemberId)) {
            return List.of();
        }

        List<Badge> badgeList = badgeQueryPort.findByBadgeByMemberId(targetMemberId);
        return badgeList.stream().map(this::toGetBadgeResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetBadgeResponse> getAllBadge() {
        return badgeQueryPort.findAll().stream()
                .map(this::toGetBadgeResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GetBadgeResponse getBadgeDetail(Long badgeId) {
        Badge badge = badgeQueryPort.findByBadgeId(badgeId);
        return toGetBadgeResponse(badge);
    }

    private GetBadgeResponse toGetBadgeResponse(Badge badge) {
        return new GetBadgeResponse(
                badge.getBadgeId().value(),
                badge.getBadgeInfo().name(),
                badge.getBadgeInfo().description(),
                badge.getBadgeImage().iconUrl(),
                badge.getBadgeType());
    }

    private Badge getBadgeDomain(CreateBadgeRequest request, String iconUrl) {
        BadgeInfo badgeInfo = new BadgeInfo(request.getName(), request.getDescription(), request.getConditionValue());
        BadgeImage badgeImage = new BadgeImage(iconUrl);
        BadgeType badgeType = request.getBadgeType();
        return Badge.withoutId(badgeInfo, badgeImage, badgeType);
    }

}