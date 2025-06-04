package com.hnc.mogak.challenge.application.port.scheduler;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.badge.event.ChallengeCompletionCountEvent;
import com.hnc.mogak.badge.event.ChallengeCompletionDurationEvent;
import com.hnc.mogak.badge.event.ChallengeCompletionOfficialEvent;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeBadgeRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeMemberRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeRepository;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import com.hnc.mogak.global.util.mapper.BadgeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeStatusScheduler {

    private final ChallengeBadgeRepository challengeBadgeRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;

    private final ApplicationEventPublisher eventPublisher;

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateChallengesByDate() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
//        LocalDate today = LocalDate.now().plusDays(1);

        updateStartChallenge(today);

        LocalDate testDate = today.plusDays(30);
        List<ChallengeEntity> completedChallenges = challengeRepository.findAllByEndDateAndStatus(
                today.minusDays(1),
//                testDate,
                ChallengeStatus.ONGOING
        );

        completedChallenges.forEach(challengeEntity -> {
            challengeEntity.updateChallengeStatus(ChallengeStatus.COMPLETED);
            challengeRepository.save(challengeEntity);

            List<Long> survivorMemberIds = challengeMemberRepository.findSurvivorMemberIds(challengeEntity.getId());

            executeDurationEvent(challengeEntity, survivorMemberIds);
            executeCountEvent(challengeEntity, survivorMemberIds);

            if (challengeEntity.isOfficial()) {
                Badge badge = BadgeMapper.mapToDomainEntity(challengeBadgeRepository.findBadgeEntityByChallengeId(challengeEntity.getId())
                        .orElseThrow(() -> new BadgeException(ErrorCode.NOT_EXISTS_BADGE)));

                eventPublisher.publishEvent(new ChallengeCompletionOfficialEvent(
                        challengeEntity.getId(),
                        survivorMemberIds,
                        badge
                ));
            }
        });
    }

    private void executeCountEvent(ChallengeEntity challengeEntity, List<Long> survivorMemberIds) {
        eventPublisher.publishEvent(new ChallengeCompletionCountEvent(challengeEntity.getId(), survivorMemberIds));
    }

    private void executeDurationEvent(ChallengeEntity challengeEntity, List<Long> survivorMemberIds) {
        int duration = calculateDuration(challengeEntity.getStartDate(), challengeEntity.getEndDate());
        eventPublisher.publishEvent(new ChallengeCompletionDurationEvent(
                challengeEntity.getId(),
                survivorMemberIds,
                duration
        ));
    }

    private void updateStartChallenge(LocalDate today) {
        challengeRepository.updateStatusForStartingChallenges(today, ChallengeStatus.BEFORE, ChallengeStatus.ONGOING);
    }

    private int calculateDuration(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

}
