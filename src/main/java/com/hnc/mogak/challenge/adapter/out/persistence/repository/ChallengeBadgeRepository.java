package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeBadgeRepository extends JpaRepository<ChallengeBadgeEntity, Long> {

    @Query("SELECT cb.badgeEntity FROM ChallengeBadgeEntity cb WHERE cb.challengeEntity.id = :challengeId")
    Optional<BadgeEntity> findBadgeEntityByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT cb.badgeEntity FROM ChallengeBadgeEntity cb WHERE cb.challengeEntity.id = :challengeId")
    Optional<BadgeEntity> findBadgeByChallengeId(@Param(value = "challengeId") Long challengeId);
}
