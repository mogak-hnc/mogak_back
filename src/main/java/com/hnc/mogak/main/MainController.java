package com.hnc.mogak.main;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "1. Main", description = "메인 페이지 관련 API")
public class MainController {

    private final MainPageService mainPageService;

    @Operation(summary = "모각존 & 챌린지 메인 조회", description = "모각존과 챌린지의 메인 화면 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<MainPageResponse> getMainPage() {
        return ResponseEntity.ok(mainPageService.getMainPageData());
    }
}