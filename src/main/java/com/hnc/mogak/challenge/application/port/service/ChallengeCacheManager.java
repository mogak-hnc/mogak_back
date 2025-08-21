package com.hnc.mogak.challenge.application.port.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.badge.application.port.out.BadgeQueryPort;
import com.hnc.mogak.badge.domain.Badge;
import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeCommonData;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeException;
import com.hnc.mogak.global.redis.RedisConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeCacheManager {

    private final ChallengeQueryPort challengeQueryPort;
    private final ChallengeMemberPort challengeMemberPort;
    private final BadgeQueryPort badgeQueryPort;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long LOCK_TTL_SECONDS = 10;

    public ChallengeCommonData updateCache(Long challengeId) {
        String lockKey = RedisConstant.CHALLENGE_DETAIL_LOCK + challengeId;
        String cacheKey = RedisConstant.CHALLENGE_DETAIL_CACHE_PREFIX + challengeId;

        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(LOCK_TTL_SECONDS));

        if (Boolean.FALSE.equals(isLocked)) {
            try {
                Thread.sleep(200);
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return objectMapper.convertValue(cached, ChallengeCommonData.class);
                }

                return getFromDatabase(challengeId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ChallengeException(ErrorCode.CACHED_FAILED);
            }
        }

        try {
            log.info("락 획득 성공, 캐시 갱신 시작: {}", challengeId);
            ChallengeCommonData commonData = getFromDatabase(challengeId);
            redisTemplate.opsForValue().set(cacheKey, commonData, Duration.ofMinutes(30));
            return commonData;

        } finally {
            redisTemplate.delete(lockKey);
        }

    }

    private ChallengeCommonData getFromDatabase(Long challengeId) {
        Challenge challenge = challengeQueryPort.findByChallengeId(challengeId);
        Badge badge = challenge.isOfficial() ? badgeQueryPort.findByChallengeId(challengeId) : null;
        List<String> memberImageList = challengeMemberPort.getMemberImageByChallengeId(challengeId, 7);
        int survivorCount = challengeMemberPort.getSurvivorCount(challengeId);
        Long challengeOwnerId = challengeQueryPort.findChallengeOwnerMemberIdByChallengeId(challengeId);
        return new ChallengeCommonData(memberImageList, challenge, survivorCount, challengeOwnerId, badge);
    }

}
