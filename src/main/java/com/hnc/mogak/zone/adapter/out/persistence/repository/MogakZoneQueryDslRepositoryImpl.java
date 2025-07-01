package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.member.adapter.out.persistence.QMemberEntity;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.*;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MogakZoneQueryDslRepositoryImpl implements MogakZoneQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisTemplate<Object, Object> redisTemplate;

//    @Override
//    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
//        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
//        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
//        QTagEntity tag = QTagEntity.tagEntity;
//        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
//        QMemberEntity member = QMemberEntity.memberEntity;
//
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (query.getTag() != null && !query.getTag().isBlank()) {
//            builder.and(zoneTag.tag.name.eq(query.getTag()));
//        }
//
//        List<Tuple> zoneInfos = queryFactory
//                .select(mogakZone.id, mogakZone.name, mogakZone.passwordRequired)
//                .from(mogakZone)
//                .leftJoin(zoneMember).on(zoneMember.mogakZoneEntity.eq(mogakZone))
//                .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                .where(builder)
//                .groupBy(mogakZone.id)
//                .orderBy(getSort(query))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        List<Long> zoneIds = zoneInfos.stream()
//                .map(t -> t.get(mogakZone.id))
//                .toList();
//
//        List<Tuple> tagTuples = queryFactory
//                .select(zoneTag.zone.id, tag.name)
//                .from(zoneTag)
//                .join(tag).on(zoneTag.tag.eq(tag))
//                .where(zoneTag.zone.id.in(zoneIds))
//                .fetch();
//
//        Map<Long, List<String>> zoneIdToTags = new HashMap<>();
//        for (Tuple tuple : tagTuples) {
//            Long zoneId = tuple.get(zoneTag.zone.id);
//            String tagName = tuple.get(tag.name);
//            zoneIdToTags.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(tagName);
//        }
//
//        List<Tuple> memberTuples = queryFactory
//                .select(zoneMember.mogakZoneEntity.id, member.imagePath)
//                .from(zoneMember)
//                .join(member).on(zoneMember.memberEntity.eq(member))
//                .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
//                .fetch();
//
//        Map<Long, List<String>> zoneIdToImages = new HashMap<>();
//        for (Tuple tuple : memberTuples) {
//            Long zoneId = tuple.get(zoneMember.mogakZoneEntity.id);
//            String imagePath = tuple.get(member.imagePath);
//            zoneIdToImages.computeIfAbsent(zoneId, k -> new ArrayList<>());
//            List<String> images = zoneIdToImages.get(zoneId);
//
//            if (images.size() <= 3) {
//                if (imagePath == null) imagePath = "Default";
//                images.add(imagePath);
//            }
//        }
//
//        List<MogakZoneSearchResponse> content = zoneInfos.stream()
//                .map(zoneInfo -> {
//                    Long zoneId = zoneInfo.get(mogakZone.id);
//                    String name = zoneInfo.get(mogakZone.name);
//                    Boolean passwordRequired = zoneInfo.get(mogakZone.passwordRequired);
//                    List<String> tags = zoneIdToTags.getOrDefault(zoneId, List.of());
//                    List<String> images = zoneIdToImages.getOrDefault(zoneId, List.of());
//                    return MogakZoneSearchResponse.builder()
//                            .mogakZoneId(zoneId)
//                            .name(name)
//                            .tagNames(tags)
//                            .memberImageUrls(images)
//                            .passwordRequired(Boolean.TRUE.equals(passwordRequired))
//                            .build();
//                }).toList();
//
//        Long total = queryFactory
//                .select(mogakZone.countDistinct())
//                .from(mogakZone)
//                .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                .where(builder)
//                .fetchOne();
//
//        long safeTotal = Optional.ofNullable(total).orElse(0L);
//        return new PageImpl<>(content, pageable, safeTotal);
//    }
//
//    private OrderSpecifier<?> getSort(MogakZoneSearchQuery query) {
//        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
//            return QZoneMemberEntity.zoneMemberEntity.count().desc();
//        } else {
//            return QMogakZoneEntity.mogakZoneEntity.createdAt.desc();
//        }
//    }

//    @Override
//    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
//        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
//        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
//        QTagEntity tag = QTagEntity.tagEntity;
//        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
//        QMemberEntity member = QMemberEntity.memberEntity;
//        QZoneSummary summary = QZoneSummary.zoneSummary;
//
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (query.getTag() != null && !query.getTag().isBlank()) {
//            builder.and(zoneTag.tag.name.eq(query.getTag()));
//
//            long totalStart = System.currentTimeMillis();
//            long start = System.currentTimeMillis();
//            List<Tuple> zoneInfos = queryFactory
//                    .select(mogakZone.id, mogakZone.name, mogakZone.passwordRequired)
//                    .from(mogakZone)
//                    .leftJoin(zoneMember).on(zoneMember.mogakZoneEntity.eq(mogakZone))
//                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                    .where(builder)
//                    .groupBy(mogakZone.id)
//                    .orderBy(getSortFromZone(query))
//                    .offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch();
//
//            long end = System.currentTimeMillis();
//            System.out.println("zoneInfos 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            List<Long> zoneIds = zoneInfos.stream()
//                    .map(t -> t.get(mogakZone.id))
//                    .toList();
//
//            List<Tuple> tagTuples = queryFactory
//                    .select(zoneTag.zone.id, tag.name)
//                    .from(zoneTag)
//                    .join(tag).on(zoneTag.tag.eq(tag))
//                    .where(zoneTag.zone.id.in(zoneIds))
//                    .fetch();
//            end = System.currentTimeMillis();
//            System.out.println("zoneIds&tagTuples 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToTags = new HashMap<>();
//            for (Tuple tuple : tagTuples) {
//                Long zoneId = tuple.get(zoneTag.zone.id);
//                String tagName = tuple.get(tag.name);
//                zoneIdToTags.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(tagName);
//            }
//            end = System.currentTimeMillis();
//            System.out.println("zoneIdToTags 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            List<Tuple> memberTuples = queryFactory
//                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
//                    .from(zoneMember)
//                    .join(member).on(zoneMember.memberEntity.eq(member))
//                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
//                    .fetch();
//            end = System.currentTimeMillis();
//            System.out.println("memberTuples 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToImages = new HashMap<>();
//            for (Tuple tuple : memberTuples) {
//                Long zoneId = tuple.get(zoneMember.mogakZoneEntity.id);
//                String imagePath = tuple.get(member.imagePath);
//                zoneIdToImages.computeIfAbsent(zoneId, k -> new ArrayList<>());
//                List<String> images = zoneIdToImages.get(zoneId);
//
//                if (images.size() <= 3) {
//                    if (imagePath == null) imagePath = "Default";
//                    images.add(imagePath);
//                }
//            }
//            end = System.currentTimeMillis();
//            System.out.println("zoneIdToImages 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            List<MogakZoneSearchResponse> content = zoneInfos.stream()
//                    .map(zoneInfo -> {
//                        Long zoneId = zoneInfo.get(mogakZone.id);
//                        String name = zoneInfo.get(mogakZone.name);
//                        Boolean passwordRequired = zoneInfo.get(mogakZone.passwordRequired);
//                        List<String> tags = zoneIdToTags.getOrDefault(zoneId, List.of());
//                        List<String> images = zoneIdToImages.getOrDefault(zoneId, List.of());
//                        return MogakZoneSearchResponse.builder()
//                                .mogakZoneId(zoneId)
//                                .name(name)
//                                .tagNames(tags)
//                                .memberImageUrls(images)
//                                .passwordRequired(Boolean.TRUE.equals(passwordRequired))
//                                .build();
//                    }).toList();
//            end = System.currentTimeMillis();
//            System.out.println("content 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//
//            JPAQuery<Long> countQuery = queryFactory
//                    .select(mogakZone.countDistinct())
//                    .from(mogakZone);
//
//            if (query.getTag() != null && !query.getTag().isBlank()) {
//                countQuery
//                        .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                        .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                        .where(tag.name.eq(query.getTag()));
//            }
//
//            Long total = countQuery.fetchOne();
//
//            end = System.currentTimeMillis();
//            System.out.println("count 쿼리 수행 시간: " + (end - start) + "ms");
//
//            long safeTotal = Optional.ofNullable(total).orElse(0L);
//
//            long totalEnd = System.currentTimeMillis();
//            System.out.println("total 쿼리 수행 시간: " + (totalEnd - totalStart) + "ms");
//
//            return new PageImpl<>(content, pageable, safeTotal);
//        } else {
//            long totalStart = System.currentTimeMillis();
//            long start = System.currentTimeMillis();
//            List<Tuple> zoneInfos = queryFactory
//                    .select(summary.id, summary.name, summary.passwordRequired)
//                    .from(summary)
//                    .orderBy(getSortFromSummary(query))
//                    .offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch();
//            long end = System.currentTimeMillis();
//            System.out.println("zoneInfos 쿼리 수행 시간: " + (end - start) + "ms");
//
//            List<Long> zoneIds = zoneInfos.stream()
//                    .map(t -> t.get(summary.id))
//                    .toList();
//
//            start = System.currentTimeMillis();
//            // 태그 정보 가져오기
//            List<Tuple> tagTuples = queryFactory
//                    .select(zoneTag.zone.id, tag.name)
//                    .from(zoneTag)
//                    .join(tag).on(zoneTag.tag.eq(tag))
//                    .where(zoneTag.zone.id.in(zoneIds))
//                    .fetch();
//            end = System.currentTimeMillis();
//            System.out.println("tagTuples 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToTags = new HashMap<>();
//            for (Tuple tuple : tagTuples) {
//                Long zoneId = tuple.get(zoneTag.zone.id);
//                String tagName = tuple.get(tag.name);
//                zoneIdToTags.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(tagName);
//            }
//            // 멤버 이미지 정보 가져오기
//            List<Tuple> memberTuples = queryFactory
//                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
//                    .from(zoneMember)
//                    .join(member).on(zoneMember.memberEntity.eq(member))
//                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
//                    .fetch();
//            end = System.currentTimeMillis();
//            System.out.println("memberTuples 쿼리 수행 시간: " + (end - start) + "ms");
//
//
//            start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToImages = new HashMap<>();
//            for (Tuple tuple : memberTuples) {
//                Long zoneId = tuple.get(zoneMember.mogakZoneEntity.id);
//                String imagePath = tuple.get(member.imagePath);
//                zoneIdToImages.computeIfAbsent(zoneId, k -> new ArrayList<>());
//                List<String> images = zoneIdToImages.get(zoneId);
//
//                if (images.size() <= 3) {
//                    if (imagePath == null) imagePath = "Default";
//                    images.add(imagePath);
//                }
//            }
//            end = System.currentTimeMillis();
//            System.out.println("zoneIdToImages 쿼리 수행 시간: " + (end - start) + "ms");
//
//            start = System.currentTimeMillis();
//            List<MogakZoneSearchResponse> content = zoneInfos.stream()
//                    .map(zoneInfo -> {
//                        Long zoneId = zoneInfo.get(summary.id);
//                        String name = zoneInfo.get(summary.name);
//                        Boolean passwordRequired = zoneInfo.get(summary.passwordRequired);
//                        List<String> tags = zoneIdToTags.getOrDefault(zoneId, List.of());
//                        List<String> images = zoneIdToImages.getOrDefault(zoneId, List.of());
//                        return MogakZoneSearchResponse.builder()
//                                .mogakZoneId(zoneId)
//                                .name(name)
//                                .tagNames(tags)
//                                .memberImageUrls(images)
//                                .passwordRequired(Boolean.TRUE.equals(passwordRequired))
//                                .build();
//                    })
//                    .toList();
//            end = System.currentTimeMillis();
//            System.out.println("content 쿼리 수행 시간: " + (end - start) + "ms");
//
//            // 전체 개수 카운트
//            start = System.currentTimeMillis();
//            Long total = queryFactory
//                    .select(summary.count())
//                    .from(summary)
//                    .fetchOne();
//            end = System.currentTimeMillis();
//            System.out.println("count 쿼리 수행 시간: " + (end - start) + "ms");
//
//            long safeTotal = Optional.ofNullable(total).orElse(0L);
//            long totalEnd = System.currentTimeMillis();
//            System.out.println("total 쿼리 수행 시간: " + (totalEnd - totalStart) + "ms");
//            return new PageImpl<>(content, pageable, safeTotal);
//        }
//
//    }

    // 현재 제일 최적화되잇음
    @Override
    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
        QMemberEntity member = QMemberEntity.memberEntity;
        QZoneSummary summary = QZoneSummary.zoneSummary;

        if (query.getTag() != null && !query.getTag().isBlank()) {
            // 🔍 태그 조건이 있을 경우
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(tag.name.eq(query.getTag()));

            // 1단계: zoneId 페이징
            List<Long> zoneIds = queryFactory
                    .select(mogakZone.id)
                    .from(mogakZone)
                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
                    .where(builder)
                    .groupBy(mogakZone.id)
                    .orderBy(getSortFromZone(query), mogakZone.id.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            if (zoneIds.isEmpty()) return Page.empty(pageable);

            // 2단계: 상세 정보 조회
            List<Tuple> zoneInfos = queryFactory
                    .select(mogakZone.id, mogakZone.name, mogakZone.passwordRequired)
                    .from(mogakZone)
                    .where(mogakZone.id.in(zoneIds))
                    .fetch();

            // 태그 조회
            Map<Long, List<String>> zoneIdToTags = queryFactory
                    .select(zoneTag.zone.id, tag.name)
                    .from(zoneTag)
                    .join(tag).on(zoneTag.tag.eq(tag))
                    .where(zoneTag.zone.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneTag.zone.id),
                            Collectors.mapping(t -> t.get(tag.name), Collectors.toList())
                    ));

            // 이미지 조회
            Map<Long, List<String>> zoneIdToImages = queryFactory
                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
                    .from(zoneMember)
                    .join(member).on(zoneMember.memberEntity.eq(member))
                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneMember.mogakZoneEntity.id),
                            Collectors.mapping(t -> {
                                String img = t.get(member.imagePath);
                                return img == null ? "Default" : img;
                            }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().limit(3).toList()))
                    ));

            // 결과 조합
            List<MogakZoneSearchResponse> content = zoneInfos.stream()
                    .map(tuple -> {
                        Long id = tuple.get(mogakZone.id);
                        return MogakZoneSearchResponse.builder()
                                .mogakZoneId(id)
                                .name(tuple.get(mogakZone.name))
                                .passwordRequired(Boolean.TRUE.equals(tuple.get(mogakZone.passwordRequired)))
                                .tagNames(zoneIdToTags.getOrDefault(id, List.of()))
                                .memberImageUrls(zoneIdToImages.getOrDefault(id, List.of()))
                                .build();
                    }).toList();

            // count
            Long total = queryFactory
                    .select(mogakZone.id.countDistinct())
                    .from(mogakZone)
                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
                    .where(builder)
                    .fetchOne();

            return new PageImpl<>(content, pageable, total == null ? 0 : total);
        } else {
                long totalStart = System.currentTimeMillis();

                // 1단계: ID만 추출 (인덱스 활용 + 페이징 안정성 확보)
                long start = System.currentTimeMillis();
                List<Long> zoneIds = queryFactory
                        .select(summary.id)
                        .from(summary)
                        .orderBy(getSortFromSummary(query), summary.id.asc()) // 안정성 있는 정렬
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

                if (zoneIds.isEmpty()) return Page.empty(pageable);
                long end = System.currentTimeMillis();
                System.out.println("zoneIds 추출 시간: " + (end - start) + "ms");

                // 2단계: 상세 정보 조회
                start = System.currentTimeMillis();
                List<Tuple> zoneInfos = queryFactory
                        .select(summary.id, summary.name, summary.passwordRequired)
                        .from(summary)
                        .where(summary.id.in(zoneIds))
                        .fetch();
                end = System.currentTimeMillis();
                System.out.println("zoneInfos 조회 시간: " + (end - start) + "ms");

                // 3단계: 태그 조회
                start = System.currentTimeMillis();
                Map<Long, List<String>> zoneIdToTags = queryFactory
                        .select(zoneTag.zone.id, tag.name)
                        .from(zoneTag)
                        .join(tag).on(zoneTag.tag.eq(tag))
                        .where(zoneTag.zone.id.in(zoneIds))
                        .fetch()
                        .stream()
                        .collect(Collectors.groupingBy(
                                tuple -> tuple.get(zoneTag.zone.id),
                                Collectors.mapping(t -> t.get(tag.name), Collectors.toList())
                        ));
                end = System.currentTimeMillis();
                System.out.println("zoneIdToTags 조회 시간: " + (end - start) + "ms");

                // 4단계: 멤버 이미지 조회 (최대 3개 제한)
                start = System.currentTimeMillis();
                Map<Long, List<String>> zoneIdToImages = queryFactory
                        .select(zoneMember.mogakZoneEntity.id, member.imagePath)
                        .from(zoneMember)
                        .join(member).on(zoneMember.memberEntity.eq(member))
                        .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
                        .fetch()
                        .stream()
                        .collect(Collectors.groupingBy(
                                tuple -> tuple.get(zoneMember.mogakZoneEntity.id),
                                Collectors.mapping(t -> {
                                    String img = t.get(member.imagePath);
                                    return img == null ? "Default" : img;
                                }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().limit(3).toList()))
                        ));
                end = System.currentTimeMillis();
                System.out.println("zoneIdToImages 조회 시간: " + (end - start) + "ms");

                // 5단계: 응답 DTO 조립
                start = System.currentTimeMillis();
                List<MogakZoneSearchResponse> content = zoneInfos.stream()
                        .map(tuple -> {
                            Long zoneId = tuple.get(summary.id);
                            return MogakZoneSearchResponse.builder()
                                    .mogakZoneId(zoneId)
                                    .name(tuple.get(summary.name))
                                    .passwordRequired(Boolean.TRUE.equals(tuple.get(summary.passwordRequired)))
                                    .tagNames(zoneIdToTags.getOrDefault(zoneId, List.of()))
                                    .memberImageUrls(zoneIdToImages.getOrDefault(zoneId, List.of()))
                                    .build();
                        })
                        .toList();
                end = System.currentTimeMillis();
                System.out.println("응답 DTO 조립 시간: " + (end - start) + "ms");

                // 6단계: 전체 개수 카운트
                start = System.currentTimeMillis();
            Object cached = redisTemplate.opsForValue().get("zone_summary_total_count");

            Long totalCount;
            if (cached == null) {
                totalCount = queryFactory
                        .select(summary.count())
                        .from(summary)
                        .fetchOne();

                redisTemplate.opsForValue().set("zone_summary_total_count", totalCount, Duration.ofMinutes(10));
            } else {
                totalCount = ((Number) cached).longValue();
            }
                end = System.currentTimeMillis();
                System.out.println("count 쿼리 시간: " + (end - start) + "ms");

                long totalEnd = System.currentTimeMillis();
                System.out.println("총 수행 시간: " + (totalEnd - totalStart) + "ms");

                return new PageImpl<>(content, pageable, Optional.ofNullable(totalCount).orElse(0L));
            }

    }

    /*
    인덱스만 적용하고 반정규화 적용하지 않았을때의 쿼리문
     */
//    @Override
//    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
//        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
//        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
//        QTagEntity tag = QTagEntity.tagEntity;
//        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
//        QMemberEntity member = QMemberEntity.memberEntity;
//        QZoneSummary summary = QZoneSummary.zoneSummary;
//
//            // 🔍 태그 조건이 있을 경우
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (query.getTag() != null && !query.getTag().isBlank()) {
//            builder.and(tag.name.eq(query.getTag()));
//        }
//
//            // 1단계: zoneId 페이징
//        long start = System.currentTimeMillis();
//            List<Long> zoneIds = queryFactory
//                    .select(mogakZone.id)
//                    .from(mogakZone)
//                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                    .where(builder)
//                    .groupBy(mogakZone.id)
//                    .orderBy(getSortFromZone(query), mogakZone.id.asc())
//                    .offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch();
//            long end = System.currentTimeMillis();
//            System.out.println("zoneIds 쿼리 수행 시간: " + (end - start) + "ms");
//
//            if (zoneIds.isEmpty()) return Page.empty(pageable);
//
//            // 2단계: 상세 정보 조회
//        start = System.currentTimeMillis();
//            List<Tuple> zoneInfos = queryFactory
//                    .select(mogakZone.id, mogakZone.name, mogakZone.passwordRequired)
//                    .from(mogakZone)
//                    .where(mogakZone.id.in(zoneIds))
//                    .fetch();
//            end = System.currentTimeMillis();
//            System.out.println("zoneInfos 쿼리 수행 시간: " + (end - start) + "ms");
//
//            // 태그 조회
//        start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToTags = queryFactory
//                    .select(zoneTag.zone.id, tag.name)
//                    .from(zoneTag)
//                    .join(tag).on(zoneTag.tag.eq(tag))
//                    .where(zoneTag.zone.id.in(zoneIds))
//                    .fetch()
//                    .stream()
//                    .collect(Collectors.groupingBy(
//                            tuple -> tuple.get(zoneTag.zone.id),
//                            Collectors.mapping(t -> t.get(tag.name), Collectors.toList())
//                    ));
//            end = System.currentTimeMillis();
//            System.out.println("zoneIdToTags 쿼리 수행 시간: " + (end - start) + "ms");
//
//            // 이미지 조회
//        start = System.currentTimeMillis();
//            Map<Long, List<String>> zoneIdToImages = queryFactory
//                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
//                    .from(zoneMember)
//                    .join(member).on(zoneMember.memberEntity.eq(member))
//                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
//                    .fetch()
//                    .stream()
//                    .collect(Collectors.groupingBy(
//                            tuple -> tuple.get(zoneMember.mogakZoneEntity.id),
//                            Collectors.mapping(t -> {
//                                String img = t.get(member.imagePath);
//                                return img == null ? "Default" : img;
//                            }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().limit(3).toList()))
//                    ));
//            end = System.currentTimeMillis();
//            System.out.println("zoneIdToImages 쿼리 수행 시간: " + (end - start) + "ms");
//
//            // 결과 조합
//        start = System.currentTimeMillis();
//            List<MogakZoneSearchResponse> content = zoneInfos.stream()
//                    .map(tuple -> {
//                        Long id = tuple.get(mogakZone.id);
//                        return MogakZoneSearchResponse.builder()
//                                .mogakZoneId(id)
//                                .name(tuple.get(mogakZone.name))
//                                .passwordRequired(Boolean.TRUE.equals(tuple.get(mogakZone.passwordRequired)))
//                                .tagNames(zoneIdToTags.getOrDefault(id, List.of()))
//                                .memberImageUrls(zoneIdToImages.getOrDefault(id, List.of()))
//                                .build();
//                    }).toList();
//            end = System.currentTimeMillis();
//            System.out.println("content 쿼리 수행 시간: " + (end - start) + "ms");
//
//            // count
//        start = System.currentTimeMillis();
//            Long total = queryFactory
//                    .select(mogakZone.id.countDistinct())
//                    .from(mogakZone)
//                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                    .where(builder)
//                    .fetchOne();
//            end = System.currentTimeMillis();
//            System.out.println("total 쿼리 수행 시간: " + (end - start) + "ms");
//
//            return new PageImpl<>(content, pageable, total == null ? 0 : total);
//
//    }

    private OrderSpecifier<?> getSortFromZone(MogakZoneSearchQuery query) {
        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
            return QMogakZoneEntity.mogakZoneEntity.participantNum.desc();
        } else {
            return QMogakZoneEntity.mogakZoneEntity.createdAt.desc();
        }
    }

    private OrderSpecifier<?> getSortFromSummary(MogakZoneSearchQuery query) {
        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
            return QZoneSummary.zoneSummary.participantNum.desc();
        } else {
            return QZoneSummary.zoneSummary.createdAt.desc();
        }
    }

}