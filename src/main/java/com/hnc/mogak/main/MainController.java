package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.MogakChallengeMainResponse;
import com.hnc.mogak.challenge.application.port.service.ChallengeService;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.service.MogakZoneQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MogakZoneQueryService mogakZoneQueryService;
    private final ChallengeService challengeService;

    @GetMapping("/api/mogak")
    public ResponseEntity<MainPage> getMainPage() {
        List<MogakZoneMainResponse> mogakZoneMainResponses = mogakZoneQueryService.getMainPage();
        List<MogakChallengeMainResponse> mogakChallengeResponses = challengeService.getMainPage();
        return ResponseEntity.status(HttpStatus.OK).body(new MainPage(mogakZoneMainResponses, mogakChallengeResponses));
    }

}