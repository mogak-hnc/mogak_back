package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.*;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.application.port.out.ChallengeCommandPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeException;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        if (command.getStartDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_DATE);
        }

        Member challengeCreator = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = ChallengeMapper.mapToDomain(command);

        Challenge savedChallenge = challengeCommandPort.persist(challengeCreator, challenge);

        join(getJoinChallengeJoinCommand(command, savedChallenge));
        return CreateChallengeResponse.build(command, savedChallenge, challengeCreator.getMemberId().value());
    }

    @Override
    public JoinChallengeResponse join(JoinChallengeCommand command) {
        Member member = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeQueryPort.findByChallengeId(command.getChallengeId());

        List<Member> members = challengeMemberPort.findMembersByChallengeId(challenge.getChallengeId().value());

        if (challenge.isAlreadyStart(LocalDate.now())) {
            throw new ChallengeException(ErrorCode.ALREADY_STARTED);
        }

        if (challenge.isAlreadyJoin(member, members)) {
            throw new ChallengeException(ErrorCode.ALREADY_JOINED);
        }

        return challengeMemberPort.join(member, challenge);
    }

    @Override
    public ChallengeDetailResponse getDetail(Long challengeId) {
        int limit = 6;
        Challenge challenge = challengeQueryPort.findByChallengeId(challengeId);
        List<String> memberImageList = challengeMemberPort.getMemberImageByChallengeId(challengeId, limit);
        int survivorCount = challengeMemberPort.getSurvivorCount(challengeId);
        List<ChallengeArticleEntity> challengeArticleEntityList = challengeArticlePort.findImagesByChallengeId(challengeId);
        List<String> imageThumbnailList = challengeArticleEntityList.stream()
                .map(entity -> entity.getChallengeImageEntityList().get(0).getImageUrl())
                .toList();

        return ChallengeDetailResponse.build(memberImageList, challenge, imageThumbnailList, survivorCount);
    }

    @Override
    public List<MogakChallengeMainResponse> getMainPage() {
        int mainChallengeLimit = 3;
        List<Challenge> topChallenges = challengeQueryPort.findTopChallengesByParticipants(mainChallengeLimit);

        int memberUrlLimit = 4;
        return topChallenges.stream()
                .map(challenge -> {
                    List<String> memberUrlList = challengeMemberPort.getMemberImageByChallengeId(challenge.getChallengeId().value(), memberUrlLimit);
                    return MogakChallengeMainResponse.builder()
                            .official(challenge.getExtraDetails().official())
                            .title(challenge.getContent().title())
                            .startDate(challenge.getChallengeDuration().startDate())
                            .endDate(challenge.getChallengeDuration().endDate())
                            .memberImageUrls(memberUrlList)
                            .build();
                }
        ).toList();
    }

    @Override
    public Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query) {
        return challengeQueryPort.searchChallenge(query);
    }

    private JoinChallengeCommand getJoinChallengeJoinCommand(CreateChallengeCommand command, Challenge savedChallenge) {
        return JoinChallengeCommand.builder()
                .memberId(command.getMemberId())
                .challengeId(savedChallenge.getChallengeId().value())
                .build();
    }

}
