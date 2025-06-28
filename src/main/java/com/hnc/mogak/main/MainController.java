package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.challenge.application.port.service.ChallengeService;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.service.MogakZoneQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "1. Main", description = "메인 페이지 관련 API")
public class MainController {

    private final MogakZoneQueryService mogakZoneQueryService;
    private final ChallengeService challengeService;

    @Operation(summary = "모각존 & 챌린지 메인 조회", description = "모각존과 챌린지의 메인 화면 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<MainPageResponse> getMainPage() {
        MogakZoneSearchQuery mogakZoneSearchQuery = MogakZoneSearchQuery.builder()
                .sort(MogakZoneSearchQuery.Sort.participant)
                .page(0)
                .size(4)
                .build();

        Page<MogakZoneSearchResponse> mogakZoneMainResponses = mogakZoneQueryService.searchMogakZone(mogakZoneSearchQuery);

        ChallengeSearchQuery challengeSearchQuery = ChallengeSearchQuery.builder()
                .sort(ChallengeSearchQuery.Sort.participant)
                .status(ChallengeStatus.BEFORE)
                .page(0)
                .size(4)
                .build();

        Page<ChallengeSearchResponse> mogakChallengeResponses = challengeService.searchChallenge(challengeSearchQuery);

        return ResponseEntity.status(HttpStatus.OK).body(new MainPageResponse(mogakZoneMainResponses, mogakChallengeResponses));
    }

}