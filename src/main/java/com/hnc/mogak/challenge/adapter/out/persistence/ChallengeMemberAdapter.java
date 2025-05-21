package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeMemberEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeMemberRepository;
import com.hnc.mogak.challenge.application.port.out.ChallengeMemberPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChallengeMemberAdapter implements ChallengeMemberPort {

    private final ChallengeMemberRepository challengeMemberRepository;

    @Override
    public JoinChallengeResponse join(Member member, Challenge challenge) {
        ChallengeMemberEntity challengeMemberEntity = ChallengeMemberEntity.builder()
                .memberEntity(MemberMapper.mapToJpaEntity(member))
                .challengeEntity(ChallengeMapper.mapToJpaEntity(challenge))
                .survivor(true)
                .build();
        challengeMemberRepository.save(challengeMemberEntity);
        return new JoinChallengeResponse(challengeMemberEntity.getChallengeEntity().getId());
    }

    @Override
    public int getSurvivorCount(Long challengeId) {
        return challengeMemberRepository.getSurvivorCount(challengeId);
    }

    @Override
    public List<String> getMemberImageByChallengeId(Long challengeId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return challengeMemberRepository.findMemberImagesByChallengeId(challengeId, pageable);
    }

    @Override
    public List<Member> findMembersByChallengeId(Long challengeId) {
        return challengeMemberRepository.findMembersByChallengeId(challengeId)
                .stream().map(MemberMapper::mapToDomainEntity).toList();
    }

    @Override
    public Map<Long, List<String>> getMemberImagesByChallengeIds(List<Long> challengeIds, int limitPerChallenge) {
        List<Object[]> results = challengeMemberRepository.findMemberImagesGroupedByChallengeIds(challengeIds, limitPerChallenge);

        Map<Long, List<String>> imageMap = new HashMap<>();
        for (Object[] row : results) {
            Long challengeId = (Long) row[0];
            String imageUrl = (String) row[1];

            imageMap.computeIfAbsent(challengeId, k -> new ArrayList<>()).add(imageUrl);
        }
        return imageMap;
    }
}
