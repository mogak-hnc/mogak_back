package com.hnc.mogak.main;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainPageScheduler {

    private final MogakZoneQueryService mogakZoneQueryService;
    private final ChallengeService challengeService;
    private final RedisTemplate<Object, Object> redisTemplate;

    private static final long TTL_SECONDS = 600;

    @Scheduled(cron = "0 */10 * * * *")
    public void cacheMainPageData() {
        try {
            log.info("[MainPageScheduler] 메인 페이지 데이터 캐싱 시작");

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

            log.info("[MainPageScheduler] 메인 페이지 캐시 완료");
        } catch (Exception e) {
            log.error("[MainPageScheduler] 메인 페이지 캐시 중 오류 발생", e);
        }
    }
}