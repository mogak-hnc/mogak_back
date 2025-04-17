package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.out.CommandChallengePort;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.application.port.out.QueryMemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService implements ChallengeUseCase {

    private final QueryMemberPort queryMemberPort;
    private final CommandChallengePort commandChallengePort;
    private final ChallengeMapper challengeMapper;

    @Override
    public CreateChallengeResponse create(CreateChallengeCommand command) {
        Member challengeCreator = queryMemberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeMapper.mapToDomain(command, challengeCreator);
        CreateChallengeResponse response = commandChallengePort.persist(challenge, challengeCreator);
        return response;
    }
}
