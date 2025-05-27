package com.hnc.mogak.badge.event;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChallengeCompletionOfficialEvent {

    private Long challengeId;
    private List<Long> memberIdList;
    private BadgeType badgeType;

}