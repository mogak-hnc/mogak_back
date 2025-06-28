package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneStatusResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.SendJoinMogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.command.ChangeStatusCommand;
import com.hnc.mogak.zone.application.port.in.command.SendChatMessageCommand;

public interface WebSocketUseCase {
    ChatMessageResponse sendMessage(SendChatMessageCommand command);

    MogakZoneStatusResponse changeStatus(ChangeStatusCommand command);

    SendJoinMogakZoneResponse sendJoinMogakZone(Long mogakZoneId);

}
