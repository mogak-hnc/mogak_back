package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleDetail;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleThumbNail;
import com.hnc.mogak.challenge.application.port.in.ChallengeArticleUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.cloud.S3PathConstants;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeArticleException;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeArticleService implements ChallengeArticleUseCase {

    private final ChallengeArticlePort challengeArticlePort;
    private final ChallengeMemberPort challengeMemberPort;
    private final MemberPort memberPort;
    private final ChallengeQueryPort challengeQueryPort;
    private final S3Service s3Service;

    @Override
    public Long create(CreateArticleCommand command) {
        validCreateArticle(command);

        Member member = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeQueryPort.findByChallengeId(command.getChallengeId());

        if (!challenge.isONGOING()) {
            throw new ChallengeArticleException(ErrorCode.ONLY_CAN_UPLOAD_WHEN_ONGOING);
        }
        List<String> imageUrls = saveImages(command);

        return challengeArticlePort.persist(member, challenge, imageUrls, command.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetChallengeArticleThumbNail> getChallengeArticlesThumbnail(Long challengeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return challengeArticlePort.getChallengeArticlesThumbnail(challengeId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public GetChallengeArticleDetail getChallengeArticleDetail(Long challengeId, Long articleId) {
        return challengeArticlePort.findChallengeArticle(challengeId, articleId);
    }

    private List<String> saveImages(CreateArticleCommand command) {
        List<MultipartFile> images = command.getImages();
        if (images.size() > 10) throw new ChallengeArticleException(ErrorCode.TOO_MANY_IMAGES);
        return images.stream().map(image -> s3Service.uploadImage(image, S3PathConstants.CHALLENGE_ARTICLE)).toList();
    }

    private void validCreateArticle(CreateArticleCommand command) {
        if (!challengeMemberPort.isMember(command.getChallengeId(), command.getMemberId())) {
            throw new ChallengeArticleException(ErrorCode.NOT_JOINED_CHALLENGE);
        }

        if (!challengeMemberPort.isSurvivor(command.getChallengeId(), command.getMemberId())) {
            throw new ChallengeArticleException(ErrorCode.NOT_SURVIVOR);
        }

        if (challengeArticlePort.isAlreadyPostToday(command.getChallengeId(), command.getMemberId())) {
            throw new ChallengeArticleException(ErrorCode.ONLY_ONE_POST_PER_DAY);
        }
    }

}