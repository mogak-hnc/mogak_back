package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeOwnerRepository extends JpaRepository<ChallengeOwnerEntity, Long> {

    @Query("SELECT co.memberEntity.id FROM ChallengeOwnerEntity co WHERE co.challengeEntity.id = :challengeId")
    Optional<Long> findChallengeOwnerMemberIdByChallengeEntityId(@Param("challengeId") Long challengeId);

    void deleteByChallengeEntity(ChallengeEntity challengeEntity);

}
