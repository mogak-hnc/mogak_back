package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {

    List<ChallengeEntity> findByStartDateAfterOrderByTotalParticipantsDesc(LocalDate date, Pageable pageable);

    @Query("UPDATE ChallengeEntity c " +
            "SET c.status = :newStatus " +
            "WHERE c.status = :currentStatus " +
            "AND c.endDate = :date")
    @Modifying
    void updateStatusForEndingChallenges(
            @Param("date") LocalDate date,
            @Param("currentStatus") ChallengeStatus currentStatus,
            @Param("newStatus") ChallengeStatus newStatus
    );


    @Query("UPDATE ChallengeEntity c " +
            "SET c.status = :newStatus " +
            "WHERE c.status = :currentStatus " +
            "AND c.startDate = :date")
    @Modifying
    void updateStatusForStartingChallenges(
            @Param("date") LocalDate date,
            @Param("currentStatus") ChallengeStatus currentStatus,
            @Param("newStatus") ChallengeStatus newStatus
    );

    List<ChallengeEntity> findAllByEndDateAndStatus(LocalDate endDate, ChallengeStatus challengeStatus);

    List<Long> findIdByStartDateAndStatus(LocalDate today, ChallengeStatus challengeStatus);

    @Query("UPDATE ChallengeEntity c " +
            "SET c.totalParticipants = :participantsNum, c.status = :challengeStatus " +
            "WHERE c.id = :challengeId")
    @Modifying
    void updateStatusAndTotalParticipants(Long challengeId, int participantsNum, ChallengeStatus challengeStatus);
}
