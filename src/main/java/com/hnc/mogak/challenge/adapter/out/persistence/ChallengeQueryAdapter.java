package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeOwnerRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeQueryDslRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeRepository;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeException;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChallengeQueryAdapter implements ChallengeQueryPort {

    private final ChallengeRepository challengeRepository;
    private final ChallengeOwnerRepository challengeOwnerRepository;
    private final ChallengeQueryDslRepository challengeQueryDslRepository;

    @Override
    public Challenge findByChallengeId(Long challengeId) {
        ChallengeEntity challengeEntity = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.NOT_EXISTS_CHALLENGE));
        return ChallengeMapper.mapToDomain(challengeEntity);
    }

    @Override
    public List<Challenge> findTopChallengesByParticipants(int limit) {
        List<ChallengeEntity> topChallenges = challengeRepository
                .findByStartDateAfterOrderByTotalParticipantsDesc(LocalDate.now(ZoneId.of("Asia/Seoul")), PageRequest.of(0, limit));

        return topChallenges.stream().map(ChallengeMapper::mapToDomain).toList();
    }

    @Override
    public Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return challengeQueryDslRepository.searchChallenge(query, pageable);
    }

    @Override
    public Long findChallengeOwnerMemberIdByChallengeId(Long challengeId) {
        return challengeOwnerRepository.findChallengeOwnerMemberIdByChallengeEntityId(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.NOT_EXISTS_CHALLENGE));
    }
}
