package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.badge.domain.vo.BadgeId;
import com.hnc.mogak.badge.domain.vo.BadgeImage;
import com.hnc.mogak.badge.domain.vo.BadgeInfo;
import org.springframework.stereotype.Component;

@Component
public class BadgeMapper {

    public static BadgeEntity mapToJpaEntity(Badge badge) {
        Long badgeId = badge.getBadgeId() == null ? null : badge.getBadgeId().value();

        return new BadgeEntity(
                badgeId,
                badge.getBadgeInfo().name(),
                badge.getBadgeInfo().description(),
                badge.getBadgeImage().iconUrl(),
                badge.getBadgeType()
        );
    }

    public static Badge mapToDomainEntity(BadgeEntity badgeEntity) {
        BadgeId badgeId = new BadgeId(badgeEntity.getId());
        BadgeInfo badgeInfo = new BadgeInfo(badgeEntity.getName(), badgeEntity.getDescription());
        BadgeImage badgeImage = new BadgeImage(badgeEntity.getIconUrl());
        return Badge.withId(badgeId, badgeInfo, badgeImage, badgeEntity.getBadgeType());
    }

}
