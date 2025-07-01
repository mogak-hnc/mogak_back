package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.cloud.S3PathConstants;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.global.monitoring.RequestContextHolder;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.application.port.in.MogakZoneCommandUseCase;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.DelegateHostCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.UpdateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.out.MogakZoneCommandPort;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.TagPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MogakZoneCommandService implements MogakZoneCommandUseCase {

    private final MogakZoneCommandPort mogakZoneCommandPort;
    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final MemberPort memberPort;
    private final TagPort tagPort;

    private final S3Service s3Service;

    @Override
    public CreateMogakZoneResponse create(CreateMogakZoneCommand command) {
        log.info("[{}] [모각존 생성 로직 실행]", RequestContextHolder.getContext().getUuid());
        Member hostMember = memberPort.loadMemberByMemberId(command.getMemberId());
        Set<TagEntity> tagEntitySet = tagPort.findOrCreateTags(command.getTagNames());

        String imageUrl = "Default";
        if (command.getImageUrl() != null) {
            imageUrl = s3Service.uploadImage(command.getImageUrl(), S3PathConstants.MOGAKZONE);
        }

        MogakZone mogakZone = createMogakZone(command, tagEntitySet, imageUrl);
        saveZoneOwner(hostMember, mogakZone);

        mogakZoneCommandPort.saveZoneSummary(mogakZone, tagEntitySet);
        return MogakZoneMapper.mapToMogakZoneResponse(mogakZone, command.getTagNames());
    }

    @Override
    public JoinMogakZoneResponse join(JoinMogakZoneCommand command) {
        log.info("[{}] [모각존 참가 로직 실행]", RequestContextHolder.getContext().getUuid());

        MogakZone mogakZone = mogakZoneQueryPort.findWithLock(command.getMogakZoneId());
        Member findMember = memberPort.loadMemberByMemberId(command.getMemberId());

        ZoneSummary zoneSummary = mogakZoneQueryPort.findSummaryWithLock(mogakZone.getZoneId().value());

        mogakZoneQueryPort.saveZoneSummaryMemberImage(
                mogakZone.getZoneId().value(),
                findMember.getMemberId().value(),
                findMember.getMemberInfo().imagePath()
        );

        validateMogakZoneJoin(command, mogakZone, zoneSummary.getParticipantNum());
        zoneSummary.increaseJoinCount();
        mogakZone.increaseJoinCount();
        return zoneMemberPort.join(mogakZone, findMember);
    }

    @Override
    public void leave(Long mogakZoneId, Long memberId) {
        log.info("[모각존 나가기 로직 실행] mogakZoneId={}, memberId={}", mogakZoneId, memberId);
        ZoneSummary zoneSummary = mogakZoneQueryPort.getSummaryDetail(mogakZoneId);
        zoneSummary.decreaseJoinCount();

        MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
        mogakZone.decreaseJoinCount();

        mogakZoneCommandPort.deleteZoneSummaryMemberImage(mogakZoneId, memberId);
        mogakZoneCommandPort.saveMogakZone(mogakZone);
        zoneMemberPort.deleteMemberByMogakZoneId(mogakZoneId, memberId);
    }

    @Override
    public Long deleteMogakZone(Long mogakZoneId, Long memberId, String role) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
        Long ownerMemberId = mogakZoneQueryPort.findZoneOwnerIdByMogakZoneId(mogakZoneId);

        if (!role.equals(AuthConstant.ROLE_ADMIN) && mogakZone.isNotHost(memberId, ownerMemberId)) {
            throw new MogakZoneException(ErrorCode.NOT_CREATOR);
        }

        mogakZoneCommandPort.deleteMogakZone(mogakZone);
        return mogakZoneId;
    }

    @Override
    public Long kickMember(Long mogakZoneId, Long requestMemberId, Long targetMemberId, String role) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(mogakZoneId);
        Long ownerMemberId = mogakZoneQueryPort.findZoneOwnerIdByMogakZoneId(mogakZoneId);

        if (!role.equals(AuthConstant.ROLE_ADMIN) && mogakZone.isNotHost(requestMemberId, ownerMemberId)) {
            throw new MogakZoneException(ErrorCode.NOT_CREATOR);
        }

        zoneMemberPort.deleteMemberByMogakZoneId(mogakZoneId, targetMemberId);
        return mogakZoneId;
    }

    @Override
    public void delegateHost(DelegateHostCommand command) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(command.getMogakZoneId());
        Long ownerMemberId = mogakZoneQueryPort.findZoneOwnerIdByMogakZoneId(command.getMogakZoneId());

        if (mogakZone.isNotHost(command.getCurrentHostId(), ownerMemberId)) {
            throw new MogakZoneException(ErrorCode.NOT_CREATOR);
        }

        boolean isMember = zoneMemberPort.isMemberInMogakZone(command.getMogakZoneId(), command.getNewHostId());
        if (!isMember) {
            throw new MogakZoneException(ErrorCode.NOT_MEMBER);
        }

        Member newOwnerMember = memberPort.loadMemberByMemberId(command.getNewHostId());

        mogakZoneCommandPort.updateHost(mogakZone.getZoneId().value(), newOwnerMember);
    }

    @Override
    public void updateMogakZone(UpdateMogakZoneCommand command) {
        Long ownerMemberId = mogakZoneQueryPort.findZoneOwnerIdByMogakZoneId(command.getMogakZoneId());
        MogakZone findMogakZone = mogakZoneQueryPort.findById(command.getMogakZoneId());
        if (findMogakZone.isNotHost(command.getRequestMemberId(), ownerMemberId)) {
            throw new MogakZoneException(ErrorCode.NOT_CREATOR);
        }

        String imageUrl = s3Service.uploadImage(command.getImageUrl(), S3PathConstants.MOGAKZONE);
        mogakZoneCommandPort.updateMogakZone(findMogakZone, imageUrl);
    }

    @Override
    public void offline(Long mogakZoneId, Long memberId) {
        zoneMemberPort.changeStatus(memberId, mogakZoneId, ZoneMemberStatus.OFFLINE);
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

    private void validateMogakZoneJoin(JoinMogakZoneCommand command, MogakZone mogakZone, int joinCount) {
        if (zoneMemberPort.isMemberInMogakZone(command.getMogakZoneId(), command.getMemberId())) {
            throw new MogakZoneException(ErrorCode.ALREADY_JOINED);
        }

        if (!mogakZone.isMatchPassword(mogakZone.getZoneInfo().password(), command.getPassword())) {
            throw new MogakZoneException(ErrorCode.INVALID_ZONE_PASSWORD);
        }

        if (mogakZone.isFullCapacity(mogakZone.getZoneConfig().maxCapacity(), joinCount)) {
            throw new MogakZoneException(ErrorCode.FULL_CAPACITY);
        }
    }

}
