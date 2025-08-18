package com.hnc.mogak.gamepoint.service;

import com.hnc.mogak.gamepoint.domain.GamePoint;
import com.hnc.mogak.gamepoint.domain.GamePointRepository;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GamePointService {

    private final GamePointRepository gamePointRepository;
    private final MemberPort memberPort;

    @Transactional
    public void chargePoint(Long memberId, Integer amount) {
        log.info("[ChargePoint]memberId={}, amount={}", memberId, amount);
        Member member = memberPort.loadMemberByMemberId(memberId);
        GamePoint gamePoint = gamePointRepository.findByMemberIdWithLock(memberId)
                .orElse(GamePoint.create(member));
        gamePoint.addPoint(amount);
        gamePointRepository.save(gamePoint);
    }

    @Transactional(readOnly = true)
    public Integer getPoint(Long memberId) {
        log.info("[GetPoint]memberId={}", memberId);
        return gamePointRepository.findByMemberId(memberId)
                .map(GamePoint::getBalance)
                .orElse(0);
    }
}