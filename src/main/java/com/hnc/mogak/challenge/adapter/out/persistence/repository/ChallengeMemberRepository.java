package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.projection.ChallengeInfoProjection;
import com.hnc.mogak.challenge.adapter.out.persistence.projection.ChallengeMembersProjection;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMemberEntity, Long> {

    @Query("SELECT cm.memberEntity.imagePath FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    List<String> findMemberImagesByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChallengeMemberEntity cm WHERE cm.survivor = true AND cm.challengeEntity.id = :challengeId")
    int getSurvivorCount(@Param("challengeId") Long challengeId);

    @Query("SELECT cm.memberEntity FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    List<MemberEntity> findMembersByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT " +
            "cm.memberEntity.id as memberId, " +
            "cm.memberEntity.nickname as nickname, " +
            "cm.memberEntity.imagePath as memberImageUrl, " +
            "cm.survivor as survivor " +
            "FROM ChallengeMemberEntity cm " +
            "WHERE cm.challengeEntity.id = :challengeId " +
            "ORDER BY cm.survivor DESC"
    )
    Page<ChallengeMembersProjection> findMembersByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    @Query(value = """
    SELECT sub.challenge_id, m.image_path
    FROM (
        SELECT
            cm.challenge_id,
            cm.member_id,
            ROW_NUMBER() OVER (PARTITION BY cm.challenge_id ORDER BY cm.challenge_member_id) AS rn
        FROM challenge_member cm
        WHERE cm.challenge_id IN (:challengeIds)
    ) AS sub
    JOIN member m ON sub.member_id = m.member_id
    WHERE sub.rn <= :limitPerChallenge
    """, nativeQuery = true)
    List<Object[]> findMemberImagesGroupedByChallengeIds(
            @Param("challengeIds") List<Long> challengeIds,
            @Param("limitPerChallenge") int limitPerChallenge
    );

    void deleteAllByChallengeEntity(ChallengeEntity challengeEntity);

    @Query("SELECT COUNT(cm) > 0 FROM ChallengeMemberEntity cm " +
            "WHERE cm.challengeEntity.id = :challengeId " +
            "AND cm.memberEntity.id = :memberId")
    boolean existsByChallengeIdAndMemberId(
            @Param("challengeId") Long challengeId,
            @Param("memberId") Long memberId
    );

    @Query("SELECT cm.memberEntity.id FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    List<Long> findSurvivorMemberIds(Long challengeId);

    @Query("SELECT COUNT(cm) FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId")
    Optional<Integer> getTotalParticipants(Long challengeId);

    @Modifying
    @Query("UPDATE ChallengeMemberEntity cm SET cm.survivor = false WHERE cm.challengeEntity.id = :challengeId AND cm.memberEntity.id = :memberId")
    void updateSurvivorStatusForMember(@Param(value = "challengeId") Long challengeId, @Param(value = "memberId") Long memberId);

    @Query("SELECT cm.survivor FROM ChallengeMemberEntity cm WHERE cm.challengeEntity.id = :challengeId AND cm.memberEntity.id = :memberId")
    Boolean isSurvivor(
            @Param(value = "challengeId") Long challengeId,
            @Param(value = "memberId") Long memberId);

    @Query("SELECT " +
            "cm.challengeEntity.id AS challengeId, " +
            "cm.challengeEntity.title AS title " +
            "FROM ChallengeMemberEntity cm " +
            "WHERE cm.memberEntity.id = :memberId " +
            "AND cm.survivor = true " +
            "AND cm.challengeEntity.status = com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus.ONGOING")
    List<ChallengeInfoProjection> findJoinedOngoingChallenges(@Param("memberId") Long memberId);

}