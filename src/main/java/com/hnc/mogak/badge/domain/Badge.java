package com.hnc.mogak.badge.domain;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.domain.vo.BadgeId;
import com.hnc.mogak.badge.domain.vo.BadgeImage;
import com.hnc.mogak.badge.domain.vo.BadgeInfo;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Badge {

    private BadgeId badgeId;
    private BadgeInfo badgeInfo;
    private BadgeImage badgeImage;
    private BadgeType badgeType;

    public static Badge withId(
            BadgeId badgeId,
            BadgeInfo badgeInfo,
            BadgeImage badgeImage,
            BadgeType badgeType
    ) {
        return new Badge(badgeId, badgeInfo, badgeImage, badgeType);
    }

    public static Badge withoutId(
            BadgeInfo badgeInfo,
            BadgeImage badgeImage,
            BadgeType badgeType
    ) {
        return new Badge(null, badgeInfo, badgeImage, badgeType);
    }

}
