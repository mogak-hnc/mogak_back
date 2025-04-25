package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import com.hnc.mogak.zone.application.port.out.*;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MogakZoneCommandService implements MogakZoneCommandUseCase {

    private final MogakZoneCommandPort mogakZoneCommandPort;
    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final MemberPort memberPort;
    private final TagPort tagPort;

    @Override
    public CreateMogakZoneResponse create(CreateMogakZoneCommand command) {
        Member hostMember = memberPort.loadMemberByMemberId(command.getMemberId());

        Set<String> tagNames = command.getTagNames();
        Set<TagEntity> tagEntitySet = tagPort.findOrCreateTags(tagNames);
        MogakZone mogakZone = MogakZoneMapper.mapToDomainWithoutId(command);
        mogakZone = mogakZoneCommandPort.createMogakZone(mogakZone, tagEntitySet);
        mogakZoneCommandPort.saveZoneOwner(hostMember, mogakZone);

        join(getJoinCommand(command, hostMember, mogakZone));

        return MogakZoneMapper.mapToMogakZoneResponse(mogakZone, tagNames);
    }

    @Override
    public JoinMogakZoneResponse join(JoinMogakZoneCommand command) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(command.getMogakZoneId());

        // 이미 가입되어 있는 회원이라면 중복X 구현X

        mogakZone.validateLoginRequired(mogakZone.getZoneConfig().loginRequired(), command.getMemberId());
        mogakZone.validatePassword(mogakZone.getZoneInfo().password(), command.getPassword());
        int maxCapacity = mogakZone.getZoneConfig().maxCapacity();
        int currMemberCount = zoneMemberPort.getZoneMemberCount(command.getMogakZoneId());
        mogakZone.validateJoinable(maxCapacity, currMemberCount);

        // 비로그인은 아직 구현X
        Member findMember = memberPort.loadMemberByMemberId(command.getMemberId());
        return zoneMemberPort.join(mogakZone, findMember);
    }

    private JoinMogakZoneCommand getJoinCommand(CreateMogakZoneCommand command, Member hostMember, MogakZone mogakZone) {
        return JoinMogakZoneCommand.builder()
                .memberId(hostMember.getMemberId().value())
                .mogakZoneId(mogakZone.getZoneId().value())
                .password(command.getPassword())
                .build();
    }

}
