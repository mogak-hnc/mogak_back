package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMemberEntity, Long> {
}
