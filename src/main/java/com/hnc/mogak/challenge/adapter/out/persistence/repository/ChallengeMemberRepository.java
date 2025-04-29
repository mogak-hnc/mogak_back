package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMemberEntity, Long> {

    @Query("SELECT cm.memberEntity.imagePath FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    List<String> findMemberImagesByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChallengeMemberEntity cm WHERE cm.survivor = true AND cm.challengeEntity.id = :challengeId")
    int getSurvivorCount(@Param("challengeId") Long challengeId);

    @Query("SELECT cm.memberEntity FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    List<MemberEntity> findMembersByChallengeId(@Param("challengeId") Long challengeId);

}
