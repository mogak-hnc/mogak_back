package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageRequest;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;
import com.hnc.mogak.zone.application.port.service.event.JoinMogakZoneEvent;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.application.port.service.event.CreateMogakZoneEvent;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import com.hnc.mogak.zone.application.port.out.*;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MogakZoneCommandService implements MogakZoneCommandUseCase {

    private final MogakZoneCommandPort mogakZoneCommandPort;
    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final MemberPort memberPort;
    private final ChatPort chatPort;
    private final TagPort tagPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CreateMogakZoneResponse create(CreateMogakZoneCommand command) {
        Member hostMember = memberPort.loadMemberByMemberId(command.getMemberId());
        Set<TagEntity> tagEntitySet = tagPort.findOrCreateTags(command.getTagNames());

        MogakZone mogakZone = createMogakZone(command, tagEntitySet);
        getSaveZoneOwner(hostMember, mogakZone);

        publishZoneCreationToRedis(command, mogakZone);
        join(getJoinCommand(command, hostMember, mogakZone));
        return MogakZoneMapper.mapToMogakZoneResponse(mogakZone, command.getTagNames());
    }

    @Override
    public JoinMogakZoneResponse join(JoinMogakZoneCommand command) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(command.getMogakZoneId());
        List<ZoneMember> zoneMemberList = zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZone.getZoneId().value());

        validateMogakZoneJoin(command, mogakZone, zoneMemberList);

        Member findMember = memberPort.loadMemberByMemberId(command.getMemberId());
        publishJoinInfoToRedis(mogakZone);
        return zoneMemberPort.join(mogakZone, findMember);
    }

    @Override
    public ChatMessageResponse sendMessage(SendChatMessageCommand command) {
        Member member = memberPort.loadMemberByMemberId(command.getMemberId());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        chatPort.save(
                member.getMemberId().value(),
                command.getMogakZoneId(),
                member.getMemberInfo().nickname(),
                member.getMemberInfo().imagePath(),
                command.getMessage(),
                now
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedNow = now.format(formatter);

        return ChatMessageResponse.builder()
                .memberId(member.getMemberId().value())
                .mogakZoneId(command.getMogakZoneId())
                .nickname(member.getMemberInfo().nickname())
                .imageUrl(member.getMemberInfo().imagePath())
                .message(command.getMessage())
                .now(formattedNow)
                .build();
    }

    private JoinMogakZoneCommand getJoinCommand(CreateMogakZoneCommand command, Member hostMember, MogakZone mogakZone) {
        return JoinMogakZoneCommand.builder()
                .memberId(hostMember.getMemberId().value())
                .mogakZoneId(mogakZone.getZoneId().value())
                .password(command.getPassword())
                .build();
    }

    private void getSaveZoneOwner(Member hostMember, MogakZone mogakZone) {
        mogakZoneCommandPort.saveZoneOwner(hostMember, mogakZone);
    }

    private MogakZone createMogakZone(CreateMogakZoneCommand command, Set<TagEntity> tagEntitySet) {
        MogakZone mogakZone = MogakZoneMapper.mapToDomainWithoutId(command);
        mogakZone = mogakZoneCommandPort.createMogakZone(mogakZone, tagEntitySet);
        return mogakZone;
    }

    private void publishZoneCreationToRedis(CreateMogakZoneCommand command, MogakZone mogakZone) {
        eventPublisher.publishEvent(
                new CreateMogakZoneEvent(
                        this,
                        mogakZone.getZoneId().value(),
                        mogakZone.getZoneInfo().name(),
                        command.getTagNames()
                )
        );
    }

    private void publishJoinInfoToRedis(MogakZone mogakZone) {
        eventPublisher.publishEvent(
                new JoinMogakZoneEvent(
                        this,
                        mogakZone.getZoneId().value()
                )
        );
    }

    private static void validateMogakZoneJoin(JoinMogakZoneCommand command, MogakZone mogakZone, List<ZoneMember> zoneMemberList) {
        if (mogakZone.isAlreadyJoined(command.getMemberId(), zoneMemberList)) {
            throw new MogakZoneException(ErrorCode.ALREADY_JOINED);
        }

        if (mogakZone.isLoginRequired(mogakZone.getZoneConfig().loginRequired(), command.getMemberId())) {
            throw new MogakZoneException(ErrorCode.LOGIN_REQUIRED_FOR_JOIN);
        }

        if (!mogakZone.isMatchPassword(mogakZone.getZoneInfo().password(), command.getPassword())) {
            throw new MogakZoneException(ErrorCode.INVALID_ZONE_PASSWORD);
        }

        if (mogakZone.isCapacityAvailableForJoin(mogakZone.getZoneConfig().maxCapacity(), zoneMemberList.size())) {
            throw new MogakZoneException(ErrorCode.FULL_CAPACITY);
        }
    }

}
