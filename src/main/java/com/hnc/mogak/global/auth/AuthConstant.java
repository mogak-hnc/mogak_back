package com.hnc.mogak.global.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstant {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER  = "Bearer ";
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ACCESS_ONLY_MEMBER_OR_ADMIN = "hasAnyRole('MEMBER', 'ADMIN')";
    public static final String ACCESS_ONLY_ADMIN = "hasAnyRole('ADMIN')";
    public static final String ACCESS_ONLY_MEMBER = "hasAnyRole('MEMBER')";
}
