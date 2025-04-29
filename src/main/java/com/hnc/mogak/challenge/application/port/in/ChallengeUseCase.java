package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeDetailResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.MogakChallengeMainResponse;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;

import java.util.List;

public interface ChallengeUseCase {

    CreateChallengeResponse create(CreateChallengeCommand command);

    JoinChallengeResponse join(JoinChallengeCommand command);

    ChallengeDetailResponse getDetail(Long challengeId);

    List<MogakChallengeMainResponse> getMainPage();

}
