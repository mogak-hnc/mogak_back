package com.hnc.mogak.badge.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ChallengeCompletionDurationEvent {

    private Long challengeId;
    private List<Long> memberIdList;
    private int challengeDuration;

}

