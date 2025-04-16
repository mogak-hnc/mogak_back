package com.hnc.mogak.member.util;

import com.hnc.mogak.member.application.port.out.QueryMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.concurrent.ThreadLocalRandom.current;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private static final int MAX_ATTEMPTS = 10;

    private static final String[] ADJECTIVES = {
            "집중하는", "성실한", "부지런한", "끈기있는", "열정적인", "차분한", "지적인", "노력하는", "똑똑한", "계획적인"
    };

    private static final String[] NOUNS = {
            "공부왕", "책벌레", "필기왕", "암기요정", "문제해결자", "수험생", "시험장인", "스터디리더", "지식탐험가", "집중마스터"
    };

    private final QueryMemberPort queryMemberPort;

    public String generate() {

        int attempt = MAX_ATTEMPTS;
        while (attempt-- > 0) {
            String nickname = ADJECTIVES[current().nextInt(ADJECTIVES.length)] + " " + NOUNS[current().nextInt(NOUNS.length)];
            if (!queryMemberPort.existsByNickname(nickname)) {
                return nickname;
            }
        }

        return "유저 #" + UUID.randomUUID();
    }

}
