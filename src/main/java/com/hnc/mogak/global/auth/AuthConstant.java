package com.hnc.mogak.global.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstant {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER  = "Bearer ";
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
