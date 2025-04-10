package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberId;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import org.springframework.stereotype.Component;

@Component
class MemberMapper {

    MemberEntity mapToJpaEntity(Member member) {
        MemberInfo memberInfo = member.getMemberInfo();
        PlatformInfo platformInfo = member.getPlatformInfo();
        Role role = member.getRole();
        return new MemberEntity(
                null,
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

    Member mapToDomainEntity(MemberEntity memberEntity) {
        MemberId memberId = new MemberId(memberEntity.getMemberId());
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
