package com.hnc.mogak.zone.application.port.service.event;

import com.hnc.mogak.global.redis.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MogakZoneEventListener {

    private final RedisTemplate<String, Long> redisTemplate;

    @TransactionalEventListener
    public void handleCreateMogakZoneEvent(CreateMogakZoneEvent event) {
        redisTemplate.opsForZSet().add(RedisConstant.ZONE_PARTICIPANT_COUNT, event.getMogakZoneId(), 0);
    }

    @TransactionalEventListener
    public void handleJoinMogakZoneEvent(JoinMogakZoneEvent event) {
        redisTemplate.opsForZSet().incrementScore(RedisConstant.ZONE_PARTICIPANT_COUNT, event.getMogakZoneId(), 1);
    }

}
