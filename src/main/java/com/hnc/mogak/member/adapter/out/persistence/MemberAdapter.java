package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MemberException;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberAdapter implements MemberPort {

    private final MemberRepository memberRepository;

    @Override
    public Member loadMemberByProviderId(String providerId) {
        MemberEntity memberEntity = memberRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_EXISTS_MEMBER));

        if (memberEntity.isWithdrawn()) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_DELETED);
        }

        return MemberMapper.mapToDomainEntity(memberEntity);
    }

    @Override
    public Long persist(Member member) {
        MemberEntity save = memberRepository.save(MemberMapper.mapToJpaEntity(member));
        return save.getId();
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

        return MemberMapper.mapToDomainEntity(memberEntity);
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = loadMemberByMemberId(memberId);
        member.deleteMember();
        memberRepository.save(MemberMapper.mapToJpaEntity(member));
    }

}
