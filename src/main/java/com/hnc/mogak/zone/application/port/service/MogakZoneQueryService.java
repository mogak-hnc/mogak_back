package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.GetMogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MogakZoneQueryService implements MogakZoneQueryUseCase {

    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;

    @Override
    public MogakZoneDetailResponse getDetail(GetMogakZoneDetailQuery detailQuery) {
        List<String> tagNames = mogakZoneQueryPort.getTags(detailQuery.getMogakZoneId());
        MogakZone mogakZone = mogakZoneQueryPort.findById(detailQuery.getMogakZoneId());
        ZoneOwner zoneOwner = mogakZoneQueryPort.findByMogakZoneId(detailQuery.getMogakZoneId());
        List<ZoneMember> zoneMemberList =  zoneMemberPort.findAllZoneMembersByMogakZoneId(detailQuery.getMogakZoneId());

        return ZoneMemberMapper.mapToMogakZoneDetailResponse(tagNames, mogakZone, zoneOwner, zoneMemberList);
    }

}