package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.adapter.in.web.dto.*;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChallengeUseCase {

    CreateChallengeResponse create(CreateChallengeCommand command);

    JoinChallengeResponse join(JoinChallengeCommand command);

    ChallengeDetailResponse getDetail(Long challengeId);

    List<MogakChallengeMainResponse> getMainPage();

    Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query);

}
