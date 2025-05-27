package com.hnc.mogak.badge.adapter.out.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeType {
    FIRST_CHALLENGE("첫 챌린지", "첫 번째 챌린지를 시작한 사용자에게 주어지는 뱃지"),
    WEEK_CHALLENGER("일주일 챌린지 달성", "7일 이상 30일 이하 챌린지를 완료한 사용자에게 주어지는 뱃지"),
    MONTH_CHALLENGER("한달 챌린지 달성", "30일 이상 챌린지를 완료한 사용자에게 주어지는 뱃지");
//    EARLY_BIRD("얼리버드", "공식 챌린지 미라클 모닝 뱃지");

    private final String displayName;
    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }

    public String getImageUrl() {
        return "/images/badges/" + this.getKey() + ".png";
    }
}
