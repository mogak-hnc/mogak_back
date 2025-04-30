package com.hnc.mogak.zone.adapter.in.web;


import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/zone")
@Tag(name = "3. MogakZone", description = "모각존 생성 및 조회 API")
public class MogakZoneQueryController {

    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

    @Operation(summary = "모각존 메인 페이지 조회", description = "메인 화면에서 표시할 모각존 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MogakZoneMainResponse>> getMogakZoneMainPage() {
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getMainPage());
    }

}
