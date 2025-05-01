package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeQueryDslRepository {

    Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query, Pageable pageable);

}
