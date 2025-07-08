package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleDetail;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleThumbNail;
import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;
import org.springframework.data.domain.Page;

public interface ChallengeArticleUseCase {

    Long create(CreateArticleCommand command);

    Page<GetChallengeArticleThumbNail> getChallengeArticlesThumbnail(Long challengeId, int page, int size);

    GetChallengeArticleDetail getChallengeArticleDetail(Long challengeId, Long articleId);

    Boolean hasWrittenArticleToday(Long challengeId, Long memberId);

}
