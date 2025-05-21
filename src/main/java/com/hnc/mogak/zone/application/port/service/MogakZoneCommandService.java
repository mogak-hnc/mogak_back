package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeException;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneStatusResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.application.port.in.command.ChangeStatusCommand;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;
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
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final S3Service s3Service;

    @Override
    public CreateMogakZoneResponse create(CreateMogakZoneCommand command) {
        Member hostMember = memberPort.loadMemberByMemberId(command.getMemberId());
        Set<TagEntity> tagEntitySet = tagPort.findOrCreateTags(command.getTagNames());

        String imageUrl = "Default";
        if (command.getImageUrl() != null) {
            imageUrl = s3Service.uploadImage(command.getImageUrl(), "mogakzone");
        }

        MogakZone mogakZone = createMogakZone(command, tagEntitySet, imageUrl);
        saveZoneOwner(hostMember, mogakZone);

        mogakZoneCommandPort.saveZoneSummary(mogakZone, tagEntitySet);
        join(getJoinCommand(command, hostMember, mogakZone));
        return MogakZoneMapper.mapToMogakZoneResponse(mogakZone, command.getTagNames());
    }

    @Override
    public JoinMogakZoneResponse join(JoinMogakZoneCommand command) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(command.getMogakZoneId());
        ZoneSummary zoneSummary = mogakZoneQueryPort.getSummaryDetail(mogakZone.getZoneId().value());
        Member findMember = memberPort.loadMemberByMemberId(command.getMemberId());
        mogakZoneQueryPort.saveZoneSummaryMemberImage(
                mogakZone.getZoneId().value(),
                findMember.getMemberId().value(),
                findMember.getMemberInfo().imagePath()
        );
        zoneSummary.increaseJoinCount();
        List<ZoneMember> zoneMemberList = zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZone.getZoneId().value());
        validateMogakZoneJoin(command, mogakZone, zoneMemberList);

        return zoneMemberPort.join(mogakZone, findMember);
    }

    @Override
    public void leave(Long mogakZoneId, Long memberId) {
        ZoneSummary zoneSummary = mogakZoneQueryPort.getSummaryDetail(mogakZoneId);
        zoneSummary.decreaseJoinCount();
        mogakZoneCommandPort.deleteZoneSummaryMemberImage(mogakZoneId, memberId);
        zoneMemberPort.deleteMemberByMogakZoneId(mogakZoneId, memberId);
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

    @Override
    public MogakZoneStatusResponse changeStatus(ChangeStatusCommand command) {
        zoneMemberPort.changeStatus(command.getMemberId(), command.getMogakZoneId(), command.getStatus());
        return MogakZoneStatusResponse.builder()
                .status(command.getStatus())
                .mogakZoneId(command.getMogakZoneId())
                .memberId(command.getMemberId())
                .build();
    }

    @Override
    public Long deleteMogakZone(Long mogakZoneId, Long memberId, String role) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
        Long ownerMemberId = mogakZoneQueryPort.findZoneOwnerIdByMogakZoneId(mogakZoneId);

        if (!role.equals(AuthConstant.ROLE_ADMIN) && !mogakZone.isCreator(memberId, ownerMemberId)) {
            throw new ChallengeException(ErrorCode.NOT_CREATOR);
        }

        mogakZoneCommandPort.deleteMogakZone(mogakZone);
        return mogakZoneId;
    }

    private JoinMogakZoneCommand getJoinCommand(CreateMogakZoneCommand command, Member hostMember, MogakZone mogakZone) {
        return JoinMogakZoneCommand.builder()
                .memberId(hostMember.getMemberId().value())
                .mogakZoneId(mogakZone.getZoneId().value())
                .password(command.getPassword())
                .build();
    }

    private void saveZoneOwner(Member hostMember, MogakZone mogakZone) {
        mogakZoneCommandPort.saveZoneOwner(hostMember, mogakZone);
    }

    private MogakZone createMogakZone(CreateMogakZoneCommand command, Set<TagEntity> tagEntitySet, String imageUrl) {
        MogakZone mogakZone = MogakZoneMapper.mapToDomainWithoutId(command, imageUrl);
        return mogakZoneCommandPort.createMogakZone(mogakZone, tagEntitySet);
    }

    private void validateMogakZoneJoin(JoinMogakZoneCommand command, MogakZone mogakZone, List<ZoneMember> zoneMemberList) {
        if (mogakZone.isAlreadyJoined(command.getMemberId(), zoneMemberList)) {
            throw new MogakZoneException(ErrorCode.ALREADY_JOINED);
        }

        if (!mogakZone.isMatchPassword(mogakZone.getZoneInfo().password(), command.getPassword())) {
            throw new MogakZoneException(ErrorCode.INVALID_ZONE_PASSWORD);
        }

        if (mogakZone.isCapacityAvailableForJoin(mogakZone.getZoneConfig().maxCapacity(), zoneMemberList.size())) {
            throw new MogakZoneException(ErrorCode.FULL_CAPACITY);
        }
    }

}
