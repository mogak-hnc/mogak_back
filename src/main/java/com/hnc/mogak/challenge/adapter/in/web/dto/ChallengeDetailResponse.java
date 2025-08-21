package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeDetailResponse {

    private boolean official;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalParticipants;
    private int survivorCount;
    private List<String> memberImageList;
    private boolean isJoined;
    private Long challengeOwnerId;
    private ChallengeStatus status;
    private BadgeInfo badgeInfo;
    private boolean survive;

    public static ChallengeDetailResponse build(
            ChallengeCommonData data,
            boolean isJoined,
            boolean isSurvive) {

        Badge badge = data.getBadge();
        BadgeInfo badgeInfo = null;
        if (badge != null) {
            badgeInfo = new BadgeInfo(
                    badge.getBadgeId().value(),
                    badge.getBadgeInfo().name(),
                    badge.getBadgeImage().iconUrl()
            );
        }
        Challenge challenge = data.getChallenge();
        return new ChallengeDetailResponse(
                challenge.getExtraDetails().official(),
                challenge.getContent().title(),
                challenge.getChallengeDuration().startDate(),
                challenge.getChallengeDuration().endDate(),
                challenge.getExtraDetails().totalParticipants(),
                data.getSurvivorCount(),
                data.getMemberImageList(),
                isJoined,
                data.getChallengeOwnerId(),
                challenge.getStatus(),
                badgeInfo,
                isSurvive
        );
    }
//    public static ChallengeDetailResponse build(
//            List<String> memberImageList,
//            Challenge challenge,
//            int survivorCount,
//            boolean isJoined,
//            Long challengeOwnerId,
//            Badge badge, boolean isSurvive) {
//
//        BadgeInfo badgeInfo = null;
//        if (badge != null) {
//            badgeInfo = new BadgeInfo(
//                    badge.getBadgeId().value(),
//                    badge.getBadgeInfo().name(),
//                    badge.getBadgeImage().iconUrl()
//            );
//        }
//
//        return new ChallengeDetailResponse(
//                challenge.getExtraDetails().official(),
//                challenge.getContent().title(),
//                challenge.getChallengeDuration().startDate(),
//                challenge.getChallengeDuration().endDate(),
//                challenge.getExtraDetails().totalParticipants(),
//                survivorCount,
//                memberImageList,
//                isJoined,
//                challengeOwnerId,
//                challenge.getStatus(),
//                badgeInfo,
//                isSurvive
//        );
//    }

    @Getter
    @AllArgsConstructor
    static class BadgeInfo {
        private Long badgeId;
        private String name;
        private String badgeImageUrl;
    }

}