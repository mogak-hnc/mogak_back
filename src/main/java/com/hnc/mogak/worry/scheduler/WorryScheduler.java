package com.hnc.mogak.worry.scheduler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.hnc.mogak.global.redis.RedisConstant.*;

@Component
public class WorryScheduler {

    private final TaskScheduler taskScheduler;
    private final RedisTemplate<Object, Object> redisTemplate;

    public WorryScheduler(
            @Qualifier("worryTaskScheduler") TaskScheduler taskScheduler,
            RedisTemplate<Object, Object> redisTemplate) {
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }


    public void scheduleWorryDeletion(Integer worryId, LocalDateTime deleteAt) {
        Instant deleteAtInstant = deleteAt.atZone(ZoneId.systemDefault()).toInstant();
        taskScheduler.schedule(() -> deleteWorry(worryId), deleteAtInstant);
    }

    private void deleteWorry(Integer worryId) {
        redisTemplate.opsForZSet().remove(WORRY_RECENT_SORT_KEY, worryId);
        redisTemplate.opsForZSet().remove(WORRY_EMPATHY_RANKING_KEY, worryId);
        redisTemplate.delete(WORRY_EMPATHY_USER_KEY_PREFIX + worryId);
        redisTemplate.delete(WORRY_COMMENT_ID_KEY + worryId);
    }

}
