package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.out.ChallengeOwnerPort;
import com.hnc.mogak.challenge.application.port.out.CommandChallengePort;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.application.port.out.QueryMemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService implements ChallengeUseCase {

    private final QueryMemberPort queryMemberPort;
    private final CommandChallengePort commandChallengePort;
    private final ChallengeOwnerPort challengeOwnerPort;

    private final ChallengeMapper challengeMapper;

    @Override
    public CreateChallengeResponse create(CreateChallengeCommand command) {
        Member challengeCreator = queryMemberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeMapper.mapToDomain(command);
        Challenge savedChallenge = commandChallengePort.persist(challenge);
        Long createMemberId = challengeOwnerPort.persist(challengeCreator, savedChallenge);

        return CreateChallengeResponse.builder()
                .challengeId(savedChallenge.getChallengeId().value())
                .title(savedChallenge.getContent().title())
                .description(savedChallenge.getContent().description())
                .creatorMemberId(createMemberId)
                .startDate(savedChallenge.getChallengeDuration().startDate())
                .endDate(savedChallenge.getChallengeDuration().endDate())
                .official(command.isOfficial())
                .build();
    }

}
