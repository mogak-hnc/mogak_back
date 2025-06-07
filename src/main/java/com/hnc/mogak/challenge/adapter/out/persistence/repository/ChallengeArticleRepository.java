package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChallengeArticleRepository extends JpaRepository<ChallengeArticleEntity, Long> {

    @EntityGraph(attributePaths = "memberEntity")
    @Query("SELECT ca FROM ChallengeArticleEntity ca WHERE ca.challengeEntity.id = :challengeId")
    Page<ChallengeArticleEntity> getChallengeArticlesByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    void deleteByChallengeEntity(ChallengeEntity challengeEntity);

    @EntityGraph(attributePaths = {"memberEntity", "challengeImageEntityList"})
    @Query("SELECT ca FROM ChallengeArticleEntity ca " +
            "WHERE ca.challengeEntity.id = :challengeId AND ca.id = :articleId")
    ChallengeArticleEntity findByChallengeIdAndArticleId(
            @Param(value = "challengeId") Long challengeId, @Param(value = "articleId") Long articleId);

    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END " +
            "FROM ChallengeArticleEntity ca " +
            "where ca.challengeEntity.id = :challengeId " +
            "AND ca.memberEntity.id = :memberId " +
            "AND ca.createdAt " +
            "BETWEEN :startOfDay And :endOfDay")
    boolean isAlreadyPostToday(@Param(value = "challengeId") Long challengeId,
                               @Param(value = "memberId") Long memberId,
                               @Param(value = "startOfDay") LocalDateTime startOfDay,
                               @Param(value = "endOfDay") LocalDateTime endOfDay);


}