package com.hnc.mogak.member.adapter.in.web.dto;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor

@NoArgsConstructor
@Getter
public class MemberInfoResponse {

    private Long memberId;
    private String imageUrl;
    private String nickname;
    private boolean showBadge;
    List<BadgeEntity> badgeEntityList;

}