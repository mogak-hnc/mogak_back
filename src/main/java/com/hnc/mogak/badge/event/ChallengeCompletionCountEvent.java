package com.hnc.mogak.badge.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ChallengeCompletionCountEvent {

    private Long challengeId;
    private List<Long> memberIdList;

}
