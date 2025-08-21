package com.hnc.mogak.gamepoint.service;

import com.hnc.mogak.gamepoint.domain.GamePoint;
import com.hnc.mogak.gamepoint.domain.GamePointRepository;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class GamePointService {

    private final GamePointRepository gamePointRepository;
    private final MemberPort memberPort;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final TransactionTemplate transactionTemplate;

    public void chargePoint(Long memberId, Integer amount, String orderNo) {
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(orderNo + "point", "lock", Duration.ofMinutes(3));
        if (!Boolean.TRUE.equals(isFirst)) return;

        log.info("[ChargePoint]memberId={}, amount={}", memberId, amount);
        Member member = memberPort.loadMemberByMemberId(memberId);

        transactionTemplate.executeWithoutResult(status -> {
            GamePoint gamePoint = gamePointRepository.findByMemberIdWithLock(memberId)
                    .orElse(GamePoint.create(member));
            gamePoint.addPoint(amount);
            gamePointRepository.save(gamePoint);
        });

    }

    @Transactional(readOnly = true)
    public Integer getPoint(Long memberId) {
        log.info("[GetPoint]memberId={}", memberId);
        return gamePointRepository.findByMemberId(memberId)
                .map(GamePoint::getBalance)
                .orElse(0);
    }

}