package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.TagPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MogakZoneQueryService implements MogakZoneQueryUseCase {

    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final ChatPort chatPort;
    private final TagPort tagPort;

    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public MogakZoneDetailResponse getDetail(MogakZoneDetailQuery detailQuery) {
        List<String> tagNames = mogakZoneQueryPort.getTags(detailQuery.getMogakZoneId());
        MogakZone mogakZone = mogakZoneQueryPort.findById(detailQuery.getMogakZoneId());
        ZoneOwner zoneOwner = mogakZoneQueryPort.findByMogakZoneId(detailQuery.getMogakZoneId());
        List<ZoneMember> zoneMemberList =  zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(detailQuery.getMogakZoneId());

        List<ChatMessageResponse> chatHistoryResponses = chatPort.loadMessagesByMogakZoneId(mogakZone.getZoneId().value());
        return ZoneMemberMapper.mapToMogakZoneDetailResponse(tagNames, mogakZone, zoneOwner, zoneMemberList, chatHistoryResponses);
    }

    @Override
    public List<MogakZoneMainResponse> getMainPage() {
        int size = 3;

        Set<ZSetOperations.TypedTuple<Long>> zoneWithParticipants = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RedisConstant.ZONE_PARTICIPANT_COUNT, 0, size - 1);

        if (zoneWithParticipants == null || zoneWithParticipants.isEmpty()) {
            return Collections.emptyList();
        }

        return zoneWithParticipants.stream()
                .map(tuple -> {
                    Long mogakZoneId = Long.valueOf(Objects.requireNonNull(tuple.getValue()).toString());
                    MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
                    List<ZoneMember> zoneMemberList = zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZoneId);

                    List<String> imageUrls = new ArrayList<>();
                    for (int i = 0; i < Math.min(zoneMemberList.size(), size); i++) {
                        imageUrls.add(zoneMemberList.get(i).getMember().getMemberInfo().imagePath());
                    }

                    List<String> tagNames = tagPort.findTagNameByMogakZoneId(mogakZoneId);
                    return new MogakZoneMainResponse(tagNames, mogakZone.getZoneInfo().name(), imageUrls);
                })
                .toList();
    }

}