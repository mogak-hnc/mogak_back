package com.hnc.mogak.zone.application.port.service;

import com.hnc.mogak.global.monitoring.RequestContextHolder;
import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.command.GetMessageQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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

        boolean isJoined = zoneMemberPort.isMemberInMogakZone(detailQuery.getMogakZoneId(), detailQuery.getMemberId());
        boolean passwordEnabled = mogakZone.getZoneConfig().passwordEnabled();

        return ZoneMemberMapper.mapToMogakZoneDetailResponse(
                tagNames, mogakZone, zoneOwner, zoneMemberList, isJoined, passwordEnabled
        );
    }

//    @Override
//    public List<MogakZoneMainResponse> getMainPage() {
//
//        int size = 4;
//
//        List<ZoneSummary> summaryList = mogakZoneQueryPort.findTopZoneSummariesByJoinCount(size);
//        List<Long> zoneIds = summaryList.stream()
//                .map(ZoneSummary::getMogakZoneId)
//                .toList();
//
//        Map<Long, List<String>> imageMap = mogakZoneQueryPort.getZoneMemberImagesByZoneIds(zoneIds, size);
//
//        List<MogakZoneMainResponse> responses = new ArrayList<>();
//        for (ZoneSummary zoneSummary : summaryList) {
////            List<String> tagNameList = List.of(zoneSummary.getTagNames().split(" "));
//            Long zoneId = zoneSummary.getMogakZoneId();
//            List<String> memberImageUrlList = imageMap.getOrDefault(zoneId, List.of());
//
////            responses.add(new MogakZoneMainResponse(
////                    zoneId,
////                    tagNameList,
////                    zoneSummary.getName(),
////                    memberImageUrlList,
////                    zoneSummary.isPasswordRequired()
////            ));
//        }
//
//        return responses;
//    }

//    @Override
//    public List<MogakZoneSearchResponse> getMainPage() {
//        MogakZoneSearchQuery query = MogakZoneSearchQuery.builder()
//                .sort(MogakZoneSearchQuery.Sort.participant)
//                .page(0)
//                .size(4)
//                .build();
//
//        return new ArrayList<>();
//    }

    //=====================================//
//    @Override
//    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery) {
//        return mogakZoneQueryPort.searchMogakZone(mogakZoneSearchQuery);
//    }

    @Override
    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery) {
        return mogakZoneQueryPort.searchMogakZone(mogakZoneSearchQuery);
    }

    @Override
    public List<TagNameResponse> getTagNames() {
        return mogakZoneQueryPort.getPopularTags();
    }

    @Override
    public Page<ChatMessageResponse> getMessage(GetMessageQuery getMessageQuery) {
        MogakZone mogakZone = mogakZoneQueryPort.findById(getMessageQuery.getMogakZoneId());
        return chatPort.loadMessagesByMogakZoneId(mogakZone.getZoneId().value(), getMessageQuery.getPage(), getMessageQuery.getSize());
    }
}