package com.hnc.mogak.zone.adapter.in.web;


import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.GetMogakZoneDetailQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/zone")
public class MogakZoneQueryController {

    private final JwtUtil jwtUtil;
    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

    @GetMapping("/{id}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<MogakZoneDetailResponse> getMogakZoneDetail(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @PathVariable(name = "id") Long mogakZoneId) {

        String memberId = jwtUtil.getMemberId(token);
        String nickname = jwtUtil.getNickname(token);

        GetMogakZoneDetailQuery detailQuery = GetMogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .nickname(nickname)
                .memberId(Long.parseLong(memberId))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getDetail(detailQuery));
    }

}
