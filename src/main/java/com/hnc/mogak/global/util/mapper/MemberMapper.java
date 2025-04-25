package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberId;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static MemberEntity mapToJpaEntity(Member member) {
        MemberInfo memberInfo = member.getMemberInfo();
        PlatformInfo platformInfo = member.getPlatformInfo();
        Role role = member.getRole();
        Long memberId = member.getMemberId() != null ? member.getMemberId().value() : null;
        return new MemberEntity(
                memberId,
                memberInfo.nickname(),
                memberInfo.email(),
                memberInfo.password(),
                memberInfo.imagePath(),
                memberInfo.name(),
                platformInfo.provider(),
                platformInfo.providerId(),
                role.value()
        );
    }

    public static Member mapToDomainEntity(MemberEntity memberEntity) {
        MemberId memberId = new MemberId(memberEntity.getId());
        MemberInfo memberInfo = new MemberInfo(
                memberEntity.getNickname(),
                memberEntity.getEmail(),
                memberEntity.getPassword(),
                memberEntity.getImagePath(),
                memberEntity.getName()
        );
        PlatformInfo platformInfo = new PlatformInfo(
                memberEntity.getProvider(),
                memberEntity.getProviderId()
        );
        Role role = new Role(memberEntity.getRole());
        return Member.withId(memberId, memberInfo, platformInfo, role);
    }

}
