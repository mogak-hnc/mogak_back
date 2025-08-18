package com.hnc.mogak.gamepoint.service;

import com.hnc.mogak.gamepoint.domain.GamePoint;
import com.hnc.mogak.gamepoint.domain.GamePointRepository;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GamePointService {

    private final GamePointRepository gamePointRepository;
    private final MemberPort memberPort;

    @Transactional
    public void chargePoint(Long memberId, Integer amount) {
        Member member = memberPort.loadMemberByMemberId(memberId);
        GamePoint gamePoint = gamePointRepository.findByMemberIdWithLock(memberId)
                .orElse(GamePoint.create(member));
        gamePoint.addPoint(amount);
        gamePointRepository.save(gamePoint);
    }

    @Transactional(readOnly = true)
    public Integer getPoint(Long memberId) {
        return gamePointRepository.findByMemberId(memberId)
                .map(GamePoint::getBalance)
                .orElse(0);
    }
}