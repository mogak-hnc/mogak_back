package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChallengeQueryPort {

    Challenge findByChallengeId(Long challengeId);

    List<Challenge> findTopChallengesByParticipants(int limit);

    Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query);

    Long findChallengeOwnerMemberIdByChallengeId(Long challengeId);

}
