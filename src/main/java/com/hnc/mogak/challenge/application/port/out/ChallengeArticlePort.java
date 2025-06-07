package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleDetail;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleThumbNail;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeArticlePort {

    Long persist(Member member, Challenge challenge, List<String> imageUrls, String description);

    Page<GetChallengeArticleThumbNail> getChallengeArticlesThumbnail(Long challengeId, Pageable pageable);

    GetChallengeArticleDetail findChallengeArticle(Long challengeId, Long articleId);

    boolean isAlreadyPostToday(Long challengeId, Long memberId);

}