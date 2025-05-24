package com.hnc.mogak.worry.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.global.auth.AuthConstant;
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
    public WorryCommentResponse createComment(CreateWorryCommentRequest request, Long memberId, Integer worryId) {
        validateWorryId(worryId);

        WorryCommentResponse worryCommentResponse = getCommentResponse(request, memberId);
        saveComment(worryId, worryCommentResponse);

        return worryCommentResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public WorryArticleResponse getWorryArticle(Integer worryId, String memberId) {
        validateWorryId(worryId);

        CreateWorryCommand command = getCreateWorryCommand(worryId);
        Integer empathyScore = (Objects.requireNonNull(redisTemplate.opsForZSet().score(WORRY_EMPATHY_RANKING_KEY, worryId))).intValue();
        List<Integer> restTime = getRestTime(command);

        return getWorryDetailResponse(worryId, memberId, command, empathyScore, restTime);
    }

    @Override
    public PageResponse<WorryCommentResponse> getWorryComments(Integer worryId, int page, int size) {
        validateWorryId(worryId);
        List<WorryCommentResponse> commentList = getCommentListByWorryId(worryId);

        int start = page * size;
        int end = Math.min(start + size, commentList.size());

        if (start >= commentList.size()) {
            return PageResponse.of(new ArrayList<>(), page, size, commentList.size());
        }

        List<WorryCommentResponse> pagedList = commentList.subList(start, end);

        return PageResponse.of(pagedList, page, size, commentList.size());
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

    @Override
    public WorryArticleDeleteResponse deleteWorryArticle(Integer worryId, Long memberId, String role) {
        validateWorryId(worryId);
        CreateWorryCommand createWorryCommand = getCreateWorryCommand(worryId);

        if (!role.equals(AuthConstant.ROLE_ADMIN) && !createWorryCommand.getMemberId().equals(memberId)) {
            throw new WorryException(ErrorCode.NOT_CREATOR);
        }

        redisTemplate.delete(WORRY_ID_SEQ_KEY + worryId);
        redisTemplate.delete(WORRY_COMMENT_ID_KEY + worryId);
        redisTemplate.delete(WORRY_EMPATHY_USER_KEY_PREFIX + worryId);
        redisTemplate.opsForZSet().remove(WORRY_RECENT_SORT_KEY, worryId);
        redisTemplate.opsForZSet().remove(WORRY_EMPATHY_RANKING_KEY, worryId);

        return new WorryArticleDeleteResponse(worryId);
    }

    @Override
    public WorryCommentDeleteResponse deleteWorryComment(Integer worryId, Long memberId, Integer commentId, String role) {
        validateWorryId(worryId);

        List<WorryCommentResponse> commentList = getCommentListByWorryId(worryId);

        boolean isExist = false;
        for (WorryCommentResponse comment : commentList) {
            if (!Objects.equals(comment.getCommentId(), commentId)) continue;

            if (!role.equals(AuthConstant.ROLE_ADMIN) && !comment.getMemberId().equals(memberId)) {
                throw new WorryException(ErrorCode.NOT_CREATOR);
            }

            isExist = true;
            commentList.remove(comment);
            break;
        }

        if (!isExist) throw new WorryException(ErrorCode.NOT_EXISTS_COMMENT);

        redisTemplate.opsForValue().set(WORRY_COMMENT_ID_KEY + worryId, commentList, getWorryArticleTTL(worryId));
        return new WorryCommentDeleteResponse(worryId, commentId);
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
        return getCommentListByWorryId(worryId).size();
    }

    private CreateWorryCommand buildWorryCommand(CreateWorryRequest request, Long memberId, LocalDateTime creationTime) {
        WorryDuration worryDuration = request.getDuration();
        Duration duration = worryDuration == null ? Duration.ofHours(24) : worryDuration.getDuration();

        return CreateWorryCommand.builder()
                .memberId(memberId)
                .title(request.getTitle())
                .body(request.getBody())
                .duration(duration)
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

    private void saveComment(Integer worryId, WorryCommentResponse worryCommentResponse) {
        String commentKey = WORRY_COMMENT_ID_KEY + worryId;
        List<WorryCommentResponse> commentList = getCommentListByWorryId(worryId);
        commentList.add(worryCommentResponse);
        redisTemplate.opsForValue().set(commentKey, commentList, getWorryArticleTTL(worryId));
    }

    private WorryCommentResponse getCommentResponse(CreateWorryCommentRequest request, Long memberId) {
        return WorryCommentResponse.builder()
                .memberId(memberId)
                .commentId(generateId(WORRY_COMMENT_ID_SEQ_KEY))
                .comment(request.getComment())
                .createdAt(LocalDateTime.now(SEOUL_ZONE))
                .build();
    }
    
    private List<WorryCommentResponse> getCommentListByWorryId(Integer worryId) {
        String commentKey = WORRY_COMMENT_ID_KEY + worryId;
        Object fromValue = redisTemplate.opsForValue().get(commentKey);
        List<WorryCommentResponse> commentList;

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

    private WorryArticleResponse getWorryDetailResponse(
            Integer worryId, String memberId, CreateWorryCommand command,
            Integer score, List<Integer> restTime) {
        boolean hasEmpathized = memberId !=
                null && redisTemplate.opsForHash().hasKey(WORRY_EMPATHY_USER_KEY_PREFIX + worryId, memberId);

        return WorryArticleResponse.builder()
                .title(command.getTitle())
                .body(command.getBody())
                .empathyCount(score)
                .restTime(restTime)
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