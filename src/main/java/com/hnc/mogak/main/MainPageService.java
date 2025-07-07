package com.hnc.mogak.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.global.redis.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public MainPageResponse getMainPageData() {
        Object cached = redisTemplate.opsForValue().get(RedisConstant.MAIN_PAGE_CACHE);
        if (cached != null) {
            return objectMapper.convertValue(cached, MainPageResponse.class);
        }

        return null;
    }

}