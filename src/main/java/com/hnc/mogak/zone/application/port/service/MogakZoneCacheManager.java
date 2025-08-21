package com.hnc.mogak.zone.application.port.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeCommonData;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneCommonData;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MogakZoneCacheManager {

    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long LOCK_TTL_SECONDS = 10;

    public MogakZoneCommonData updateCache(Long mogakZoneId) {
        String lockKey = RedisConstant.MOGAKZONE_DETAIL_LOCK + mogakZoneId;
        String commonCacheKey = RedisConstant.MOGAKZONE_DETAIL_CACHE_PREFIX + mogakZoneId;

        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(LOCK_TTL_SECONDS));

        if (Boolean.FALSE.equals(isLocked)) {
            try {
                Thread.sleep(200);
                Object cachedData = redisTemplate.opsForValue().get(commonCacheKey);
                if (cachedData != null) {
                    return objectMapper.convertValue(commonCacheKey, MogakZoneCommonData.class);
                }
                return getFromDatabase(mogakZoneId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MogakZoneException(ErrorCode.CACHED_FAILED);
            }
        }

        try {
            log.info("모각존 락 획득 성공, 캐시 갱신 시작: {}", mogakZoneId);
            MogakZoneCommonData commonData = getFromDatabase(mogakZoneId);
            redisTemplate.opsForValue().set(commonCacheKey, commonData, Duration.ofMinutes(30));
            return commonData;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private MogakZoneCommonData getFromDatabase(Long mogakZoneId) {
        List<String> tagNames = mogakZoneQueryPort.getTags(mogakZoneId);
        MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
        ZoneOwner zoneOwner = mogakZoneQueryPort.findZoneOwnerByMogakZoneId(mogakZoneId);
        List<ZoneMember> zoneMemberList = zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZoneId);
        boolean passwordEnabled = mogakZone.getZoneConfig().passwordEnabled();

        return new MogakZoneCommonData(tagNames, mogakZone, zoneOwner, zoneMemberList, passwordEnabled);
    }

}