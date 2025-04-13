package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByProviderId(String providerId);

    boolean existsByProviderId(String providerId);

    boolean existsByNickname(String nickname);

}
