package com.hnc.mogak.badge.adapter.in.web.dto;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBadgeResponse {

    private Long badgeId;
    private String name;
    private String description;
    private String iconUrl;
    private BadgeType badgeType;

}
