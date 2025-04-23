package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinResponse;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.application.port.in.command.JoinMogakZoneCommand;

public interface MogakZoneCommandUseCase {

    CreateMogakZoneResponse create(CreateMogakZoneCommand command);

    JoinResponse join(JoinMogakZoneCommand command);

}
