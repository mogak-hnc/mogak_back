package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.application.port.out.QueryMemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.application.port.in.MogakZoneUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.out.MogakZonePort;
import com.hnc.mogak.zone.application.port.out.TagPort;
import com.hnc.mogak.zone.domain.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MogakZoneService implements MogakZoneUseCase {

    private final MogakZonePort mogakZonePort;
    private final TagPort tagPort;
    private final MogakZoneMapper mogakZoneMapper;
    private final QueryMemberPort queryMemberPort;

    @Override
    public MogakZoneResponse create(CreateMogakZoneCommand command) {
        Member findMember = queryMemberPort.loadMemberByMemberId(command.getMemberId());
        Set<String> tagNames = command.getTagNames();
        Set<TagEntity> tagEntitySet = tagPort.findOrCreateTags(tagNames);
        MogakZone mogakZone = mogakZoneMapper.mapToDomain(command, findMember);

        return mogakZonePort.createMogakZone(mogakZone, tagEntitySet);
    }

}
