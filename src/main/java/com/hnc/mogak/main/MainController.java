package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.MogakChallengeMainResponse;
import com.hnc.mogak.challenge.application.port.service.ChallengeService;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.service.MogakZoneQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "1. Main", description = "메인 페이지 관련 API")
public class MainController {

    private final MogakZoneQueryService mogakZoneQueryService;
    private final ChallengeService challengeService;

    @Operation(summary = "모각존 & 챌린지 메인 조회", description = "모각존과 챌린지의 메인 화면 정보를 조회합니다.")
    @GetMapping("/api/mogak")
    public ResponseEntity<MainPage> getMainPage() {
        List<MogakZoneMainResponse> mogakZoneMainResponses = mogakZoneQueryService.getMainPage();
        List<MogakChallengeMainResponse> mogakChallengeResponses = challengeService.getMainPage();
        return ResponseEntity.status(HttpStatus.OK).body(new MainPage(mogakZoneMainResponses, mogakChallengeResponses));
    }

}