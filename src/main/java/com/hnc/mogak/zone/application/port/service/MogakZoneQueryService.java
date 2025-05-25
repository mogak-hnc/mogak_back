package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.out.*;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MogakZoneQueryService implements MogakZoneQueryUseCase {

    private final MogakZoneQueryPort mogakZoneQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final ChatPort chatPort;

    @Override
    public MogakZoneDetailResponse getDetail(MogakZoneDetailQuery detailQuery) {
        List<String> tagNames = mogakZoneQueryPort.getTags(detailQuery.getMogakZoneId());
        MogakZone mogakZone = mogakZoneQueryPort.findById(detailQuery.getMogakZoneId());
        ZoneOwner zoneOwner = mogakZoneQueryPort.findZoneOwnerByMogakZoneId(detailQuery.getMogakZoneId());
        List<ZoneMember> zoneMemberList =  zoneMemberPort.findAllZoneMembersWithMembersByMogakZoneId(detailQuery.getMogakZoneId());

        List<ChatMessageResponse> chatHistoryResponses = chatPort.loadMessagesByMogakZoneId(mogakZone.getZoneId().value());

        boolean isJoined = zoneMemberPort.isMemberInMogakZone(detailQuery.getMogakZoneId(), detailQuery.getMemberId());
        boolean passwordEnabled = mogakZone.getZoneConfig().passwordEnabled();

        return ZoneMemberMapper.mapToMogakZoneDetailResponse(
                tagNames, mogakZone, zoneOwner, zoneMemberList, chatHistoryResponses, isJoined, passwordEnabled
        );
    }

    @Override
    public List<MogakZoneMainResponse> getMainPage() {
        int size = 4;

        List<ZoneSummary> summaryList = mogakZoneQueryPort.findTopZoneSummariesByJoinCount(size);
        List<Long> zoneIds = summaryList.stream()
                .map(ZoneSummary::getMogakZoneId)
                .toList();

        Map<Long, List<String>> imageMap = mogakZoneQueryPort.getZoneMemberImagesByZoneIds(zoneIds, size);

        List<MogakZoneMainResponse> responses = new ArrayList<>();
        for (ZoneSummary zoneSummary : summaryList) {
            List<String> tagNameList = List.of(zoneSummary.getTagNames().split(" "));
            Long zoneId = zoneSummary.getMogakZoneId();
            List<String> memberImageUrlList = imageMap.getOrDefault(zoneId, List.of());

            responses.add(new MogakZoneMainResponse(
                    zoneId,
                    tagNameList,
                    zoneSummary.getName(),
                    memberImageUrlList,
                    zoneSummary.isPasswordRequired()
            ));
        }

        return responses;
    }

//    @Override
//    public List<MogakZoneMainResponse> getMainPage() {
//        int size = 3;
//        List<ZoneSummary> summaryList = mogakZoneQueryPort.findTopZoneSummariesByJoinCount(size);
//        List<MogakZoneMainResponse> responses = new ArrayList<>();
//
//        for (ZoneSummary zoneSummary : summaryList) {
//            List<String> tagNameList = List.of(zoneSummary.getTagNames().split(" "));
//            Long mogakZoneId = zoneSummary.getMogakZoneId();
//            List<String> memberImageUrlList = mogakZoneQueryPort.getZoneMemberImagesBySize(mogakZoneId, size);
//
//            MogakZoneMainResponse response = new MogakZoneMainResponse(
//                    zoneSummary.getMogakZoneId(),
//                    tagNameList,
//                    zoneSummary.getName(),
//                    memberImageUrlList,
//                    zoneSummary.isPasswordRequired()
//            );
//
//            responses.add(response);
//        }
//
//        return responses;
//    }

    @Override
    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery) {
        return mogakZoneQueryPort.searchMogakZone(mogakZoneSearchQuery);
    }

    @Override
    public List<TagNameResponse> getTagNames() {
        return mogakZoneQueryPort.getPopularTags();
    }

}