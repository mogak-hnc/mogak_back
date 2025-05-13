package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneStatusResponse;
import com.hnc.mogak.zone.application.port.in.command.ChangeStatusCommand;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;

public interface MogakZoneCommandUseCase {

    CreateMogakZoneResponse create(CreateMogakZoneCommand command);

    JoinMogakZoneResponse join(JoinMogakZoneCommand command);

    ChatMessageResponse sendMessage(SendChatMessageCommand command);

    MogakZoneStatusResponse changeStatus(ChangeStatusCommand command);

    void leave(Long mogakZoneId, Long memberId);

}
