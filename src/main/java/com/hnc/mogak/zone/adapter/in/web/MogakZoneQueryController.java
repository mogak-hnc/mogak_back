package com.hnc.mogak.zone.adapter.in.web;


import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
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
public class MogakZoneQueryController {

    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

    @GetMapping
    public ResponseEntity<List<MogakZoneMainResponse>> getMogakZoneMainPage() {
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getMainPage());
    }

}
