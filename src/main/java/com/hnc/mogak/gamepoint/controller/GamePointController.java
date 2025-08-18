package com.hnc.mogak.gamepoint.controller;

import com.hnc.mogak.gamepoint.dto.ChargePointRequest;
import com.hnc.mogak.gamepoint.service.GamePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gamepoint")
public class GamePointController {

    private final GamePointService gamePointService;

    @PostMapping("/exchange")
    public ResponseEntity<Void> chargePoint(@RequestBody ChargePointRequest request) {
        gamePointService.chargePoint(request.getMemberId(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<Integer> getPoint(@PathVariable(value = "memberId") Long memberId) {
        return ResponseEntity.ok(gamePointService.getPoint(memberId));
    }

}