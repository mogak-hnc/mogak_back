package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneResponse;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;

import java.util.Set;

public interface MogakZoneUseCase {

    MogakZoneResponse create(CreateMogakZoneCommand command);

}
