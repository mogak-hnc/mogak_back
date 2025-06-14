package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.adapter.in.web.dto.*;
import com.hnc.mogak.challenge.application.port.in.command.ChallengeDeactivateCommand;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.in.query.GetChallengeMembersQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChallengeUseCase {

    CreateChallengeResponse create(CreateChallengeCommand command);

    JoinChallengeResponse join(JoinChallengeCommand command);

    ChallengeDetailResponse getDetail(Long memberId, Long challengeId);

    List<ChallengeMainResponse> getMainPage();

    Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query);

    Long deleteChallenge(Long challengeId, Long memberId, String role);

    Page<ChallengeMembersResponse> getChallengeMembers(GetChallengeMembersQuery query);

    ChallengeMemberDeactivateResponse deactivateSurvivorMember(ChallengeDeactivateCommand command);

}
