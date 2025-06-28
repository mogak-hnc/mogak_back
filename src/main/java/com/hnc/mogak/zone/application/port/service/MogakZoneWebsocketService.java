package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneStatusResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.SendJoinMogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.WebSocketUseCase;
import com.hnc.mogak.zone.application.port.in.command.ChangeStatusCommand;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MogakZoneWebsocketService implements WebSocketUseCase {

    private final MemberPort memberPort;
    private final ChatPort chatPort;
    private final ZoneMemberPort zoneMemberPort;

    @Override
    public ChatMessageResponse sendMessage(SendChatMessageCommand command) {
        log.info("[웹소켓 메세지 보내기 로직 실행] mogakZoneId={}, memberId={}", command.getMogakZoneId(), command.getMemberId());
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
        log.info("[웹소켓 변경 로직 실행] mogakZoneId={}, memberId={}", command.getMogakZoneId(), command.getMemberId());
        zoneMemberPort.changeStatus(command.getMemberId(), command.getMogakZoneId(), command.getStatus());
        return MogakZoneStatusResponse.builder()
                .status(command.getStatus())
                .mogakZoneId(command.getMogakZoneId())
                .memberId(command.getMemberId())
                .build();
    }

    @Override
    public SendJoinMogakZoneResponse sendJoinMogakZone(Long mogakZoneId) {
        log.info("[웹소켓 입장 로직 실행] mogakZoneId={}", mogakZoneId);
        List<ZoneMember> zoneMemberList =  zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(mogakZoneId);
        return ZoneMemberMapper.mapToSendJoinMogakZoneResponse(zoneMemberList);
    }

}
