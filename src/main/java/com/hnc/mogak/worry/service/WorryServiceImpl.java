package com.hnc.mogak.worry.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.WorryException;
import com.hnc.mogak.worry.dto.*;
import com.hnc.mogak.worry.service.command.CreateWorryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.hnc.mogak.global.redis.RedisConstant.*;

@Service
@Transactional
@RequiredArgsConstructor
public class WorryServiceImpl implements WorryService {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TTL_24_HOURS = Duration.ofHours(24);

    @Override
    public CreateWorryResponse create(CreateWorryRequest request, Long memberId) {
        Integer id = (Integer) redisTemplate.opsForValue().get(WORRY_ID_KEY);
        if (id == null) {
            id = 0;
        }
        Integer worryId = id + 1;
        redisTemplate.opsForValue().set(WORRY_ID_KEY, worryId, TTL_24_HOURS);

        CreateWorryCommand command = CreateWorryCommand.builder()
                .memberId(memberId)
                .title(request.getTitle())
                .body(request.getBody())
                .createdAt(LocalDateTime.now())
                .build();

        redisTemplate.opsForZSet().add(WORRY_EMPATHY_RANKING_KEY, worryId, 0);
        redisTemplate.opsForZSet().add(
                WORRY_RECENT_SORT_KEY,
                worryId,
                command.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond()
        );
        redisTemplate.opsForValue().set(WORRY_ID_KEY + worryId, command, TTL_24_HOURS);

        return new CreateWorryResponse(worryId);
    }

    @Override
    public CommentResponse createComment(CreateWorryCommentRequest request, String memberId, Integer worryId) {
        if (redisTemplate.opsForValue().get(WORRY_ID_KEY + worryId) == null) {
            throw new WorryException(ErrorCode.NOT_EXISTS_WORRY);
        }

        String commentKey = WORRY_COMMENT_ID_KEY + worryId;
        Object fromValue = redisTemplate.opsForValue().get(commentKey);

        List<CommentResponse> commentList;

        if (fromValue == null) {
            commentList = new ArrayList<>();
        } else {
            commentList = objectMapper.convertValue(
                    fromValue,
                    new TypeReference<List<CommentResponse>>() {}
            );
        }

        CommentResponse commentResponse = CommentResponse.builder()
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        commentList.add(commentResponse);
        redisTemplate.opsForValue().set(commentKey, commentList, TTL_24_HOURS);
        return commentResponse;
    }

    @Override
    public WorryDetailResponse getWorry(Integer worryId) {
        if (redisTemplate.opsForValue().get(WORRY_ID_KEY + worryId) == null) {
            throw new WorryException(ErrorCode.NOT_EXISTS_WORRY);
        }

        Object fromValue = redisTemplate.opsForValue().get(WORRY_ID_KEY + worryId);
        CreateWorryCommand command = objectMapper.convertValue(fromValue, CreateWorryCommand.class);
        Integer score = (Objects.requireNonNull(redisTemplate.opsForZSet().score(WORRY_EMPATHY_RANKING_KEY, worryId))).intValue();

        List<Integer> restTime = getRestTime(command);

        Object commentObj = redisTemplate.opsForValue().get(WORRY_COMMENT_ID_KEY + worryId);
        List<CommentResponse> commentResponses;
        if (commentObj == null) {
            commentResponses = new ArrayList<>();
        } else {
            commentResponses = objectMapper.convertValue(
                    commentObj,
                    new TypeReference<List<CommentResponse>>() {
                    }
            );
        }

        return WorryDetailResponse.builder()
                .title(command.getTitle())
                .body(command.getBody())
                .empathyCount(score)
                .restTime(restTime)
                .commentResponses(commentResponses)
                .build();
    }

    @Override
    public WorryEmpathyResponse toggleEmpathy(Integer worryId, String memberId) {
        if (redisTemplate.opsForValue().get(WORRY_ID_KEY + worryId) == null) {
            throw new WorryException(ErrorCode.NOT_EXISTS_WORRY);
        }

        String empathyHashKey = WORRY_EMPATHY_USER_KEY_PREFIX + worryId;

        Boolean hasEmpathized = redisTemplate.opsForHash().hasKey(empathyHashKey, memberId);

        if (hasEmpathized) {
            redisTemplate.opsForHash().delete(empathyHashKey, memberId);
            redisTemplate.opsForZSet().incrementScore(WORRY_EMPATHY_RANKING_KEY, worryId, -1);
        } else {
            redisTemplate.opsForHash().put(empathyHashKey, memberId, true);
            redisTemplate.opsForZSet().incrementScore(WORRY_EMPATHY_RANKING_KEY, worryId, 1);
        }

        Integer empathyCount = (Objects.requireNonNull(redisTemplate.opsForZSet().score(WORRY_EMPATHY_RANKING_KEY, worryId))).intValue();

        return WorryEmpathyResponse.builder()
                .worryId(worryId)
                .empathyCount(empathyCount)
                .hasEmpathized(!hasEmpathized)
                .build();
    }

    @Override
    public List<WorryPreview> getWorryList(String sort, int start, int end) {
        sort = sort.equals("recent") ? WORRY_RECENT_SORT_KEY : WORRY_EMPATHY_RANKING_KEY;

        Set<ZSetOperations.TypedTuple<Object>> sortingList =
                redisTemplate.opsForZSet().reverseRangeWithScores(sort, start, end);

        List<WorryPreview> worryPreviews = new ArrayList<>();

        if (sortingList == null) return worryPreviews;

        for (ZSetOperations.TypedTuple<Object> tuple : sortingList) {
            Integer worryId = (Integer) tuple.getValue();
            Object articleObj = redisTemplate.opsForValue().get(WORRY_ID_KEY + worryId);
            if (articleObj == null) {
                redisTemplate.opsForZSet().remove(WORRY_RECENT_SORT_KEY, worryId);
                redisTemplate.opsForZSet().remove(WORRY_EMPATHY_RANKING_KEY, worryId);
                continue;
            }

            CreateWorryCommand command = objectMapper.convertValue(articleObj, CreateWorryCommand.class);
            List<Integer> restTime = getRestTime(command);

            Object commentObj = redisTemplate.opsForValue().get(WORRY_COMMENT_ID_KEY + worryId);
            List<CommentResponse> commentResponses = objectMapper.convertValue(
                    commentObj,
                    new TypeReference<List<CommentResponse>>() {
                    }
            );
            int commentCount = commentResponses == null ? 0 : commentResponses.size();

            worryPreviews.add(WorryPreview.builder()
                    .title(command.getTitle())
                    .worryId(worryId)
                    .commentCount(commentCount)
                    .restTime(restTime)
                    .build());
        }



        return worryPreviews;
    }

    private static List<Integer> getRestTime(CreateWorryCommand command) {
        LocalDateTime createdAt = command.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = createdAt.plusHours(24);

        Duration remaining = Duration.between(now, deadline);

        long hours = remaining.toHours();
        long minutes = remaining.toMinutes() % 60;
        long seconds = remaining.getSeconds() % 60;
        return List.of((int) hours, (int) minutes, (int) seconds);
    }

}