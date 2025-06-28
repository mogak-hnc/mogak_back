package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.command.*;

public interface MogakZoneCommandUseCase {

    CreateMogakZoneResponse create(CreateMogakZoneCommand command);

    JoinMogakZoneResponse join(JoinMogakZoneCommand command);

    void leave(Long mogakZoneId, Long memberId);

    Long deleteMogakZone(Long mogakZoneId, Long memberId, String role);

    Long kickMember(Long mogakZoneId, Long ownerMemberId, Long targetMemberId, String role);

    void delegateHost(DelegateHostCommand command);

    void updateMogakZone(UpdateMogakZoneCommand command);


}
