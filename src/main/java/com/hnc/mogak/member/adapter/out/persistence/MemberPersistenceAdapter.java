package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MemberException;
import com.hnc.mogak.member.application.port.out.QueryMemberPort;
import com.hnc.mogak.member.application.port.out.PersistMemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberPersistenceAdapter implements QueryMemberPort, PersistMemberPort {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public Member loadMemberByProviderId(String providerId) {
        MemberEntity memberEntity = memberRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_EXISTS_MEMBER));

        return memberMapper.mapToDomainEntity(memberEntity);
    }

    @Override
    public Long persist(Member member) {
        MemberEntity save = memberRepository.save(memberMapper.mapToJpaEntity(member));
        return save.getMemberId();
    }

    @Override
    public boolean existsByProviderId(String providerId) {
        return memberRepository.existsByProviderId(providerId);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public Member loadMemberByMemberId(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_EXISTS_MEMBER));
        return memberMapper.mapToDomainEntity(memberEntity);
    }

}
