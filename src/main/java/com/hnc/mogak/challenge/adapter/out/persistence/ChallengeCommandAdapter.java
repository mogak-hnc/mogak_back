package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeMemberRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeRepository;
import com.hnc.mogak.challenge.application.port.out.CommandChallengePort;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeCommandAdapter implements CommandChallengePort {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;

    private final ChallengeMapper challengeMapper;

    @Override
    public Challenge persist(Challenge challenge) {
        ChallengeEntity savedChallengeEntity = challengeRepository.save(challengeMapper.mapToJpaEntity(challenge));
        challengeMemberRepository.save(ChallengeMemberEntity.builder()
                .challengeEntity(savedChallengeEntity)
                .build());

        return challengeMapper.mapToDomain(savedChallengeEntity);
    }

}