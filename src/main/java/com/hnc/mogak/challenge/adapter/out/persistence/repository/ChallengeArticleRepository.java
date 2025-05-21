package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeArticleRepository extends JpaRepository<ChallengeArticleEntity, Long> {

    @Query("SELECT ca FROM ChallengeArticleEntity ca WHERE ca.challengeEntity.id = :challengeId")
    List<ChallengeArticleEntity> getChallengeArticleListByChallengeId(@Param("challengeId") Long challengeId);

    void deleteByChallengeEntity(ChallengeEntity challengeEntity);

}