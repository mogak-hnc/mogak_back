package com.hnc.mogak.badge.event;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.application.port.in.BadgeUseCase;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class BadgeEventListener {

    private final BadgeUseCase badgeUseCase;

    @Async
    @TransactionalEventListener
    public void handleChallengeDurationCompletion(ChallengeCompletionDurationEvent event) {
        executeEventIfEligible(event);
    }

    @Async
    @TransactionalEventListener
    public void handleOfficialChallengeCompletion(ChallengeCompletionOfficialEvent event) {
        event.getMemberIdList()
                .forEach(memberId -> executeEvent(memberId, event.getChallengeId(), event.getBadgeType()));
    }

    private void executeEventIfEligible(ChallengeCompletionDurationEvent event) {
        event.getMemberIdList().forEach(memberId -> {
            BadgeType badgeType = getBadgeTypeFromDuration(event);
            executeEvent(memberId, event.getChallengeId(), badgeType);
        });
    }

    private BadgeType getBadgeTypeFromDuration(ChallengeCompletionDurationEvent event) {
        BadgeType badgeType;
        if (event.getChallengeDuration() >= 30) {
            badgeType = BadgeType.MONTH_CHALLENGER;
        } else if (event.getChallengeDuration() >= 7) {
            badgeType = BadgeType.WEEK_CHALLENGER;
        } else {
            badgeType = BadgeType.FIRST_CHALLENGE;
        }

        return badgeType;
    }

    private void executeEvent(Long memberId, Long challengeId, BadgeType badgeType) {
        try {
            badgeUseCase.acquireBadge(memberId, challengeId, badgeType);
        } catch (BadgeException e) {
            log.info("뱃지 예외: {}", memberId, e);
        } catch (RuntimeException e) {
            log.error("런타임 예외: {}", memberId, e);
        }
    }

//    @TransactionalEventListener
//    public void handleZoneCompletion() {
//
//    }

}