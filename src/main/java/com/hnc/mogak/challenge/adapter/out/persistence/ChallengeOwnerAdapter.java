package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeOwnerRepository;
import com.hnc.mogak.challenge.application.port.out.ChallengeOwnerPort;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeOwnerAdapter implements ChallengeOwnerPort {

    private final ChallengeOwnerRepository challengeOwnerRepository;

    private final MemberMapper memberMapper;
    private final ChallengeMapper challengeMapper;

    @Override
    public Long persist(Member member, Challenge challenge) {
        challengeOwnerRepository.save(ChallengeOwnerEntity.builder()
                .challengeEntity(challengeMapper.mapToJpaEntity(challenge))
                .memberEntity(memberMapper.mapToJpaEntity(member))
                .build());

        return member.getMemberId().value();
    }

}
