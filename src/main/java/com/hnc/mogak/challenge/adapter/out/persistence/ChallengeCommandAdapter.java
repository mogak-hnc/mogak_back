package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeOwnerRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeRepository;
import com.hnc.mogak.challenge.application.port.out.ChallengeCommandPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeCommandAdapter implements ChallengeCommandPort {

    private final ChallengeRepository challengeRepository;
    private final ChallengeOwnerRepository challengeOwnerRepository;

    @Override
    public Challenge persist(Member member, Challenge challenge) {
        ChallengeEntity savedChallengeEntity = challengeRepository.save(ChallengeMapper.mapToJpaEntity(challenge));
        saveChallengeOwner(member, savedChallengeEntity);
        return ChallengeMapper.mapToDomain(savedChallengeEntity);
    }

    private void saveChallengeOwner(Member member, ChallengeEntity challengeEntity) {
        challengeOwnerRepository.save(
                ChallengeOwnerEntity.builder()
                        .challengeEntity(challengeEntity)
                        .memberEntity(MemberMapper.mapToJpaEntity(member))
                        .build()
        );
    }

}