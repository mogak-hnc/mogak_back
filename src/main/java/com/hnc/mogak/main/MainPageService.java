package com.hnc.mogak.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.service.ChallengeService;
import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.service.MogakZoneQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final MogakZoneQueryService mogakZoneQueryService;
    private final ChallengeService challengeService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long TTL_SECONDS = 600;

    public MainPageResponse getMainPageData() {

        Object cached = redisTemplate.opsForValue().get(RedisConstant.MAIN_PAGE_CACHE);
        if (cached != null) {
            return objectMapper.convertValue(cached, MainPageResponse.class);
        }

        Page<MogakZoneSearchResponse> mogakZones = mogakZoneQueryService.searchMogakZone(
                MogakZoneSearchQuery.builder()
                        .sort(MogakZoneSearchQuery.Sort.participant)
                        .page(0).size(4)
                        .build()
        );

        Page<ChallengeSearchResponse> challenges = challengeService.searchChallenge(
                ChallengeSearchQuery.builder()
                        .sort(ChallengeSearchQuery.Sort.participant)
                        .status(ChallengeStatus.BEFORE)
                        .page(0).size(4)
                        .build()
        );

        PageResponse<MogakZoneSearchResponse> mogakZonePage = PageResponse.of(
                mogakZones.getContent(),
                mogakZones.getNumber(),
                mogakZones.getSize(),
                mogakZones.getTotalElements()
        );

        PageResponse<ChallengeSearchResponse> challengePage = PageResponse.of(
                challenges.getContent(),
                challenges.getNumber(),
                challenges.getSize(),
                challenges.getTotalElements()
        );

        MainPageResponse response = new MainPageResponse(mogakZonePage, challengePage);

        redisTemplate.opsForValue().set(RedisConstant.MAIN_PAGE_CACHE, response, TTL_SECONDS, TimeUnit.SECONDS);

        return response;
    }

}