package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeDetailResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.application.port.out.ChallengeCommandPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService implements ChallengeUseCase {

    private final MemberPort memberPort;
    private final ChallengeCommandPort challengeCommandPort;
    private final ChallengeQueryPort challengeQueryPort;
    private final ChallengeMemberPort challengeMemberPort;
    private final ChallengeArticlePort challengeArticlePort;

    @Override
    public CreateChallengeResponse create(CreateChallengeCommand command) {
        Member challengeCreator = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = ChallengeMapper.mapToDomain(command);

        Challenge savedChallenge = challengeCommandPort.persist(challengeCreator, challenge);

        join(getJoinChallengeJoinCommand(command, savedChallenge));
        return CreateChallengeResponse.build(command, savedChallenge, challengeCreator.getMemberId().value());
    }

    @Override
    public JoinChallengeResponse join(JoinChallengeCommand command) {
        Member member = memberPort.loadMemberByMemberId(command.getMemberId());

        // 멤버 중복 확인 구현 X
        Challenge challenge = challengeQueryPort.findByChallengeId(command.getChallengeId());
        return challengeMemberPort.join(member, challenge);
    }

    @Override
    public ChallengeDetailResponse getDetail(Long challengeId) {
        Challenge challenge = challengeQueryPort.findByChallengeId(challengeId);
        List<String> memberImageList = challengeMemberPort.getMemberImageByChallengeId(challengeId);
        int survivorCount = challengeMemberPort.getSurvivorCount(challengeId);
        List<ChallengeArticleEntity> challengeArticleEntityList = challengeArticlePort.findImagesByChallengeId(challengeId);
        List<String> imageThumbnailList = challengeArticleEntityList.stream()
                .map(entity -> entity.getChallengeImageEntityList().get(0).getImageUrl())
                .toList();

        return ChallengeDetailResponse.build(memberImageList, challenge, imageThumbnailList, survivorCount);
    }

    private JoinChallengeCommand getJoinChallengeJoinCommand(CreateChallengeCommand command, Challenge savedChallenge) {
        return JoinChallengeCommand.builder()
                .memberId(command.getMemberId())
                .challengeId(savedChallenge.getChallengeId().value())
                .build();
    }

}
