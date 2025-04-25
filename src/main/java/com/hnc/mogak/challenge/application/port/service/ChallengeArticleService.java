package com.hnc.mogak.challenge.application.port.service;

import com.hnc.mogak.challenge.application.port.in.ChallengeArticleUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeArticleException;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeArticleService implements ChallengeArticleUseCase {

    private final ChallengeArticlePort challengeArticlePort;
    private final MemberPort memberPort;
    private final ChallengeQueryPort challengeQueryPort;

    private static final String UPLOADS_DIR = "src/main/resources/static/uploads/photos/";
    private static final String DB_FILE_PATH = "/uploads/photos/";

    @Override
    public Long create(CreateArticleCommand command) {
        List<MultipartFile> images = command.getImages();
        List<String> imageUrls = images.stream().map(this::uploadFile).toList();
        Member member = memberPort.loadMemberByMemberId(command.getMemberId());
        Challenge challenge = challengeQueryPort.findByChallengeId(command.getChallengeId());

        return challengeArticlePort.persist(member, challenge, imageUrls, command.getDescription());
    }

    private String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename().replaceAll(" ", "");
            String filePath = UPLOADS_DIR + fileName;
            String dbFilePath = DB_FILE_PATH + fileName;

            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return dbFilePath;
        } catch (IOException e) {
            throw new ChallengeArticleException(ErrorCode.UPLOAD_FAILED);
        }
    }

}
