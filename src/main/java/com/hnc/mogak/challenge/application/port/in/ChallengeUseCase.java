package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;

public interface ChallengeUseCase {

    CreateChallengeResponse create(CreateChallengeCommand command);

}
