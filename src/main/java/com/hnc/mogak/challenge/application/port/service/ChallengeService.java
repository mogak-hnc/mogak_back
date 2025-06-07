package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.*;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.out.ChallengeCommandPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeException;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Override
    public CreateChallengeResponse create(CreateChallengeCommand command) {
        Member challengeCreator = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = ChallengeMapper.mapToDomain(command);

        Challenge savedChallenge = saveChallenge(command, challenge, challengeCreator);
        join(getJoinChallengeJoinCommand(command, savedChallenge));

        return CreateChallengeResponse.build(command, savedChallenge, challengeCreator.getMemberId().value());
    }

    @Override
    public JoinChallengeResponse join(JoinChallengeCommand command) {
        Member member = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeQueryPort.findByChallengeId(command.getChallengeId());
        List<Member> members = challengeMemberPort.findMembersByChallengeId(challenge.getChallengeId().value());

        joinValidateCheck(challenge, member, members);
        increaseParticipantCount(challenge);

        return challengeMemberPort.join(member, challenge);
    }

    @Override
    public ChallengeDetailResponse getDetail(Long memberId, Long challengeId) {
        int limit = 6;
        Challenge challenge = challengeQueryPort.findByChallengeId(challengeId);
        List<String> memberImageList = challengeMemberPort.getMemberImageByChallengeId(challengeId, limit);
        int survivorCount = challengeMemberPort.getSurvivorCount(challengeId);

        boolean isJoined = challengeMemberPort.isMember(challengeId, memberId);
        Long challengeOwnerId = challengeQueryPort.findChallengeOwnerMemberIdByChallengeId(challengeId);
        return ChallengeDetailResponse.build(memberImageList, challenge, survivorCount, isJoined, challengeOwnerId);
    }

    @Override
    public List<MogakChallengeMainResponse> getMainPage() {
        int mainChallengeLimit = 4;
        List<Challenge> topChallenges = challengeQueryPort.findTopChallengesByParticipants(mainChallengeLimit);

        int memberUrlLimit = 4;
        return topChallenges.stream()
                .map(challenge -> {
                    List<String> memberUrlList = challengeMemberPort.getMemberImageByChallengeId(challenge.getChallengeId().value(), memberUrlLimit);
                    return MogakChallengeMainResponse.builder()
                            .challengeId(challenge.getChallengeId().value())
                            .official(challenge.getExtraDetails().official())
                            .title(challenge.getContent().title())
                            .startDate(challenge.getChallengeDuration().startDate())
                            .endDate(challenge.getChallengeDuration().endDate())
                            .memberImageUrls(memberUrlList)
                            .status(challenge.getStatus())
                            .build();
                }
        ).toList();
    }

    @Override
    public Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query) {
        return challengeQueryPort.searchChallenge(query);
    }

    @Override
    public Long deleteChallenge(Long challengeId, Long memberId, String role) {
        Challenge challenge = challengeQueryPort.findByChallengeId(challengeId);
        Long memberOwnerId = challengeQueryPort.findChallengeOwnerMemberIdByChallengeId(challengeId);

        if (!role.equals(AuthConstant.ROLE_ADMIN) && !challenge.isCreator(memberId, memberOwnerId)) {
            throw new ChallengeException(ErrorCode.NOT_CREATOR);
        }

        challengeCommandPort.deleteChallenge(challenge);
        return challengeId;
    }

    private Challenge saveChallenge(CreateChallengeCommand command, Challenge challenge, Member challengeCreator) {
        Challenge savedChallenge = challengeCommandPort.persist(challenge);
        challengeCommandPort.saveChallengeOwner(challengeCreator, savedChallenge);
        saveChallengeBadgeIfOfficial(savedChallenge, command.getBadgeId());
        return savedChallenge;
    }

    private JoinChallengeCommand getJoinChallengeJoinCommand(CreateChallengeCommand command, Challenge savedChallenge) {
        return JoinChallengeCommand.builder()
                .memberId(command.getMemberId())
                .challengeId(savedChallenge.getChallengeId().value())
                .build();
    }

    private void saveChallengeBadgeIfOfficial(Challenge challenge, Long badgeId) {
        if (!challenge.getExtraDetails().official()) return;
        challengeCommandPort.saveChallengeBadge(challenge, badgeId);
    }

    private void joinValidateCheck(Challenge challenge, Member member, List<Member> members) {
        if (challenge.isAlreadyStart()) {
            throw new ChallengeException(ErrorCode.ALREADY_STARTED);
        }

        if (challenge.isAlreadyJoin(member, members)) {
            throw new ChallengeException(ErrorCode.ALREADY_JOINED);
        }
    }

    private void increaseParticipantCount(Challenge challenge) {
        challenge.increaseParticipantCount();
        challengeCommandPort.persist(challenge);
    }

}
