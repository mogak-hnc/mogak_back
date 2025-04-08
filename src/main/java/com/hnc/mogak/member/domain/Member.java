package com.hnc.mogak.member.domain;

import com.hnc.mogak.member.domain.vo.MemberId;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    private MemberId memberId;
    private MemberInfo memberInfo;
    private PlatformInfo platformInfo;
    private Role role;

}
