package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.member.domain.Member;

public interface CommandChallengePort {
    Challenge persist(Challenge challenge);

}
