package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeMemberRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeRepository;
import com.hnc.mogak.challenge.application.port.out.CommandChallengePort;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeCommandAdapter implements CommandChallengePort {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;

    private final ChallengeMapper challengeMapper;
    private final MemberMapper memberMapper;

    @Override
    public CreateChallengeResponse persist(Challenge challenge, Member challengeCreator) {
        MemberEntity memberEntity = memberMapper.mapToJpaEntity(challengeCreator);;
        ChallengeEntity savedChallengeEntity = challengeRepository.save(challengeMapper.mapToJpaEntity(challenge, memberEntity));
        challengeMemberRepository.save(ChallengeMemberEntity.builder()
                .challengeEntity(savedChallengeEntity)
                .memberEntity(memberEntity)
                .build());

        return CreateChallengeResponse.builder()
                .title(savedChallengeEntity.getTitle())
                .description(savedChallengeEntity.getDescription())
                .startDate(savedChallengeEntity.getStartDate())
                .endDate(savedChallengeEntity.getEndDate())
                .creatorMemberId(memberEntity.getMemberId())
                .build();
    }

}
