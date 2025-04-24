package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeOwnerRepository extends JpaRepository<ChallengeOwnerEntity, Long> {
}
