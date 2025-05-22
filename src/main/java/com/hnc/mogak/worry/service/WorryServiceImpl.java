package com.hnc.mogak.worry.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.WorryException;
import com.hnc.mogak.worry.dto.*;
import com.hnc.mogak.worry.scheduler.WorryScheduler;
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

    private final WorryScheduler worryScheduler;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");


    @Override
    public CreateWorryResponse create(CreateWorryRequest request, Long memberId) {
        Integer worryId = generateId(WORRY_ID_SEQ_KEY);
        
        LocalDateTime creationTime = LocalDateTime.now(SEOUL_ZONE);
        CreateWorryCommand command = buildWorryCommand(request, memberId, creationTime);

        saveWorryData(worryId, command, creationTime);
        scheduleWorryDeletion(worryId, creationTime, command.getDuration());
        return new CreateWorryResponse(worryId);
    }

    @Override
    public CommentResponse createComment(CreateWorryCommentRequest request, Long memberId, Integer worryId) {
        validateWorryId(worryId);

        CommentResponse commentResponse = getCommentResponse(request, memberId);
        saveComment(worryId, commentResponse);

        return commentResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public WorryDetailResponse getWorryDetail(Integer worryId, String memberId) {
        validateWorryId(worryId);

        CreateWorryCommand command = getCreateWorryCommand(worryId);
        Integer empathyScore = (Objects.requireNonNull(redisTemplate.opsForZSet().score(WORRY_EMPATHY_RANKING_KEY, worryId))).intValue();

        List<Integer> restTime = getRestTime(command);
        List<CommentResponse> commentResponses = getCommentListByCommentKey(worryId);

        return getWorryDetailResponse(worryId, memberId, command, empathyScore, restTime, commentResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorryPreview> getWorryList(String sort, int page, int size) {
        String sortType = getSortType(sort);

        Long totalElements = redisTemplate.opsForZSet().size(sortType);
        if (totalElements == null || totalElements == 0) {
            return PageResponse.of(new ArrayList<>(), page, size, 0);
        }

        int start = page * size;
        int end = start + size - 1;
        Set<ZSetOperations.TypedTuple<Object>> sortingList = getSortedWorries(sortType, start, end);

        if (sortingList == null)  return PageResponse.of(new ArrayList<>(), page, size, totalElements);

        List<WorryPreview> worryPreviews = sortingList.stream()
                .map(tuple -> buildWorryPreview((Integer) tuple.getValue()))
                .filter(Objects::nonNull)
                .toList();

        return PageResponse.of(worryPreviews, page, size, totalElements);
    }

    @Override
    public WorryEmpathyResponse toggleEmpathy(Integer worryId, String memberId) {
        validateWorryId(worryId);

        String empathyHashKey = WORRY_EMPATHY_USER_KEY_PREFIX + worryId;
        Boolean alreadyEmpathized = redisTemplate.opsForHash().hasKey(empathyHashKey, memberId);

        updateEmpathyStatus(worryId, memberId, alreadyEmpathized, empathyHashKey);

        return getWorryEmpathyResponse(worryId, alreadyEmpathized);
    }

    private WorryPreview buildWorryPreview(Integer worryId) {
        CreateWorryCommand createWorryCommand = getCreateWorryCommand(worryId);

        if (createWorryCommand == null) {
            return null;
        }

        Integer commentCount = getCommentCount(worryId);
        List<Integer> restTime = getRestTime(createWorryCommand);

        return WorryPreview.builder()
                .worryId(worryId)
                .title(createWorryCommand.getTitle())
                .commentCount(commentCount)
                .restTime(restTime)
                .build();
    }

    private Integer getCommentCount(Integer worryId) {
        return getCommentListByCommentKey(worryId).size();
    }

    private CreateWorryCommand buildWorryCommand(CreateWorryRequest request, Long memberId, LocalDateTime creationTime) {
        WorryDuration worryDuration = request.getDuration();
        return CreateWorryCommand.builder()
                .memberId(memberId)
                .title(request.getTitle())
                .body(request.getBody())
                .duration(worryDuration.getDuration())
                .createdAt(creationTime)
                .build();
    }

    private Integer generateId(String keyName) {
        Integer currentId = (Integer) redisTemplate.opsForValue().get(keyName);
        Integer newId = (currentId == null ? 0 : currentId) + 1;
        redisTemplate.opsForValue().set(keyName, newId);
        return newId;
    }

    private void saveWorryData(Integer worryId, CreateWorryCommand command, LocalDateTime creationTime) {
        redisTemplate.opsForValue().set(WORRY_ID_SEQ_KEY + worryId, command, command.getDuration());
        saveWorryToSortedSets(worryId, creationTime);
    }

    private void saveWorryToSortedSets(Integer worryId, LocalDateTime creationTime) {
        redisTemplate.opsForZSet().add(WORRY_EMPATHY_RANKING_KEY, worryId, 0);
        long epochSeconds = creationTime.atZone(SEOUL_ZONE).toEpochSecond();
        redisTemplate.opsForZSet().add(WORRY_RECENT_SORT_KEY, worryId, epochSeconds);
    }

    private void scheduleWorryDeletion(Integer worryId, LocalDateTime creationTime, Duration duration) {
        worryScheduler.scheduleWorryDeletion(worryId, creationTime.plus(duration));
    }

    private void saveComment(Integer worryId, CommentResponse commentResponse) {
        String commentKey = WORRY_COMMENT_ID_KEY + worryId;
        List<CommentResponse> commentList = getCommentListByCommentKey(worryId);
        commentList.add(commentResponse);
        redisTemplate.opsForValue().set(commentKey, commentList, getWorryArticleTTL(worryId));
    }

    private CommentResponse getCommentResponse(CreateWorryCommentRequest request, Long memberId) {
        return CommentResponse.builder()
                .memberId(memberId)
                .commentId(generateId(WORRY_COMMENT_ID_SEQ_KEY))
                .comment(request.getComment())
                .createdAt(LocalDateTime.now(SEOUL_ZONE))
                .build();
    }
    
    private List<CommentResponse> getCommentListByCommentKey(Integer worryId) {
        String commentKey = WORRY_COMMENT_ID_KEY + worryId;
        Object fromValue = redisTemplate.opsForValue().get(commentKey);
        List<CommentResponse> commentList;

        if (fromValue == null) {
            commentList = new ArrayList<>();
        } else {
            commentList = objectMapper.convertValue(
                    fromValue,
                    new TypeReference<>() {
                    }
            );
        }
        return commentList;
    }

    private void validateWorryId(Integer worryId) {
        if (redisTemplate.opsForValue().get(WORRY_ID_SEQ_KEY + worryId) == null) {
            throw new WorryException(ErrorCode.NOT_EXISTS_WORRY);
        }
    }

    private CreateWorryCommand getCreateWorryCommand(Integer worryId) {
        Object fromValue = redisTemplate.opsForValue().get(WORRY_ID_SEQ_KEY + worryId);
        return objectMapper.convertValue(fromValue, CreateWorryCommand.class);
    }

    private WorryDetailResponse getWorryDetailResponse(
            Integer worryId, String memberId, CreateWorryCommand command, Integer score,
            List<Integer> restTime, List<CommentResponse> commentResponses) {
        boolean hasEmpathized = redisTemplate.opsForHash().hasKey(WORRY_EMPATHY_USER_KEY_PREFIX + worryId, memberId);

        return WorryDetailResponse.builder()
                .title(command.getTitle())
                .body(command.getBody())
                .empathyCount(score)
                .restTime(restTime)
                .commentResponses(commentResponses)
                .hasEmpathized(hasEmpathized)
                .build();
    }

    private WorryEmpathyResponse getWorryEmpathyResponse(Integer worryId, Boolean hasEmpathized) {
        Integer empathyCount = (Objects.requireNonNull(redisTemplate.opsForZSet().score(WORRY_EMPATHY_RANKING_KEY, worryId))).intValue();

        return WorryEmpathyResponse.builder()
                .worryId(worryId)
                .empathyCount(empathyCount)
                .hasEmpathized(!hasEmpathized)
                .build();
    }

    private void updateEmpathyStatus(Integer worryId, String memberId, Boolean alreadyEmpathized, String empathyHashKey) {
        if (alreadyEmpathized) {
            redisTemplate.opsForHash().delete(empathyHashKey, memberId);
            redisTemplate.opsForZSet().incrementScore(WORRY_EMPATHY_RANKING_KEY, worryId, -1);
        } else {
            redisTemplate.opsForHash().put(empathyHashKey, memberId, true);
            redisTemplate.opsForZSet().incrementScore(WORRY_EMPATHY_RANKING_KEY, worryId, 1);
        }
    }

    private Set<ZSetOperations.TypedTuple<Object>> getSortedWorries(String sortType, int start, int end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(sortType, start, end);
    }

    private static String getSortType(String sort) {
        return sort.equals("recent") ? WORRY_RECENT_SORT_KEY : WORRY_EMPATHY_RANKING_KEY;
    }

    private List<Integer> getRestTime(CreateWorryCommand command) {
        LocalDateTime createdAt = command.getCreatedAt();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime deadline = createdAt.plus(command.getDuration());

        Duration remaining = Duration.between(now, deadline);

        long hours = remaining.toHours();
        long minutes = remaining.toMinutes() % 60;
        long seconds = remaining.getSeconds() % 60;
        return List.of((int) hours, (int) minutes, (int) seconds);
    }

    public Duration getWorryArticleTTL(Integer worryId) {
        return Duration.ofSeconds(redisTemplate.getExpire(WORRY_ID_SEQ_KEY + worryId));
    }

}