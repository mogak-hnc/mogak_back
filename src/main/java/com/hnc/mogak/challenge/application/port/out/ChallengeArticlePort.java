package com.hnc.mogak.challenge.application.port.out;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeImageEntity;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.member.domain.Member;

import java.util.List;

public interface ChallengeArticlePort {

    Long persist(Member member, Challenge challenge, List<String> imageUrls, String description);

    List<ChallengeArticleEntity> findImagesByChallengeId(Long challengeId);
}