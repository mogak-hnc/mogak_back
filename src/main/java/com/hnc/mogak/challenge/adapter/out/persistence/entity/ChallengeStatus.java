package com.hnc.mogak.challenge.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChallengeStatus {
    BEFORE("시작 전"),
    ONGOING("진행 중"),
    COMPLETED("완료");

    private final String description;
}