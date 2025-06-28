package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.command.GetMessageQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MogakZoneQueryUseCase {

    MogakZoneDetailResponse getDetail(MogakZoneDetailQuery detailQuery);

//    List<MogakZoneSearchResponse> getMainPage();

    Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery);

    List<TagNameResponse> getTagNames();

    Page<ChatMessageResponse> getMessage(GetMessageQuery getMessageQuery);

}