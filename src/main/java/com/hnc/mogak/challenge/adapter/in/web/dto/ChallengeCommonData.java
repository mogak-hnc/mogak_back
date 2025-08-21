package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ChallengeCommonData {
    private List<String> memberImageList;
    private Challenge challenge;
    private int survivorCount;
    private Long challengeOwnerId;
    private Badge badge;

    public ChallengeCommonData(List<String> memberImageList, Challenge challenge, int survivorCount, Long challengeOwnerId, Badge badge) {
        this.memberImageList = memberImageList;
        this.challenge = challenge;
        this.survivorCount = survivorCount;
        this.challengeOwnerId = challengeOwnerId;
        this.badge = badge;
    }

}

