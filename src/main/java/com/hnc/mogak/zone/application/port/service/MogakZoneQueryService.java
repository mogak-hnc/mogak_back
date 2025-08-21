package com.hnc.mogak.zone.application.port.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeCommonData;
import com.hnc.mogak.global.monitoring.RequestContextHolder;
import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.command.GetMessageQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MogakZoneQueryService implements MogakZoneQueryUseCase {

    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final ChatPort chatPort;
    private final MogakZoneCacheManager mogakZoneCacheManager;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public MogakZoneDetailResponse getDetail(MogakZoneDetailQuery detailQuery) {
        String commonCacheKey = RedisConstant.MOGAKZONE_DETAIL_CACHE_PREFIX + detailQuery.getMogakZoneId();
        Object cached = redisTemplate.opsForValue().get(commonCacheKey);
        MogakZoneCommonData commonData;

        if (cached != null) {
            commonData = objectMapper.convertValue(cached, MogakZoneCommonData.class);
        } else {
            commonData = mogakZoneCacheManager.updateCache(detailQuery.getMogakZoneId());
        }

        boolean isJoined = zoneMemberPort.isMemberInMogakZone(detailQuery.getMogakZoneId(), detailQuery.getMemberId());

        return ZoneMemberMapper.mapToMogakZoneDetailResponse(commonData, isJoined);
    }

    @Override
    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery) {
        return mogakZoneQueryPort.searchMogakZone(mogakZoneSearchQuery);
    }

    @Override
    public List<TagNameResponse> getTagNames() {
        return mogakZoneQueryPort.getPopularTags();
    }

    @Override
    public Page<ChatMessageResponse> getMessage(GetMessageQuery getMessageQuery) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(getMessageQuery.getMogakZoneId());
        return chatPort.loadMessagesByMogakZoneId(mogakZone.getZoneId().value(), getMessageQuery.getPage(), getMessageQuery.getSize());
    }
}