package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MemberException;
import com.hnc.mogak.member.application.port.out.LoadMemberPort;
import com.hnc.mogak.member.application.port.out.PersistMemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class MemberPersistenceAdapter implements LoadMemberPort, PersistMemberPort {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public Member loadMemberByProviderId(String providerId) {
        MemberEntity memberEntity = memberRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_EXISTS_MEMBER));
        return memberMapper.mapToDomainEntity(memberEntity);
    }

    @Override
    public Long persistMember(Member member) {
        MemberEntity save = memberRepository.save(memberMapper.mapToJpaEntity(member));
        return save.getMemberId();
    }

    @Override
    public boolean existsByProviderId(String providerId) {
        return memberRepository.existsByProviderId(providerId);
    }
}
