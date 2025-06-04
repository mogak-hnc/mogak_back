package com.hnc.mogak.badge.event;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.application.port.in.BadgeUseCase;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.global.util.mapper.BadgeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class BadgeEventListener {

    private final BadgeUseCase badgeUseCase;
    private final BadgeQueryPort badgeQueryPort;

    @Async
    @TransactionalEventListener
    public void handleChallengeDurationCompletion(ChallengeCompletionDurationEvent event) {
        int challengeDays = event.getChallengeDuration();
        Badge durationBadge = findTopDurationBadgeByChallengeDays(challengeDays);
        if (durationBadge == null) return;

        giveBadge(event.getMemberIdList(), event.getChallengeId(), durationBadge);
    }

    @Async
    @TransactionalEventListener
    public void handleOfficialChallengeCompletion(ChallengeCompletionOfficialEvent event) {
        List<Long> memberIdList = event.getMemberIdList();
        for (Long memberId : memberIdList) {
            badgeUseCase.acquireBadge(memberId, event.getChallengeId(), event.getBadge());
        }
    }

    @Async
    @TransactionalEventListener
    public void handleCountChallengeCompletion(ChallengeCompletionCountEvent event) {
        List<Long> memberIds = event.getMemberIdList();
        Map<Long, Integer> badgeCountByMemberId = badgeQueryPort.findBadgeCountByMemberIds(memberIds);

        for (Long memberId : memberIds) {
            int badgeCount = badgeCountByMemberId.getOrDefault(memberId, 0);
            Optional<BadgeEntity> badgeByCountNumber = badgeQueryPort.findBadgeByCountNumber(BadgeType.COUNT, badgeCount);
            if (badgeByCountNumber.isEmpty()) continue;

            Badge badge = BadgeMapper.mapToDomainEntity(badgeByCountNumber.get());
            badgeUseCase.acquireBadge(memberId, event.getChallengeId(), badge);
        }

    }

    private Badge findTopDurationBadgeByChallengeDays(int challengeDays) {
        List<BadgeEntity> rawDurationBadge = badgeQueryPort.findTopDurationBadge(BadgeType.DURATION, challengeDays);
        if (rawDurationBadge.isEmpty()) return null;

        return BadgeMapper.mapToDomainEntity(rawDurationBadge.get(0));
    }

    private void giveBadge(List<Long> memberIdList, Long challengeId, Badge badge) {
        memberIdList.forEach(memberId ->
                badgeUseCase.acquireBadge(
                        memberId,
                        challengeId,
                        badge));
    }

}