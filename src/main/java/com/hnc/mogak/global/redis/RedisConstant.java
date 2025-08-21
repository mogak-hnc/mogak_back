package com.hnc.mogak.global.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstant {

    public static final String WORRY_EMPATHY_RANKING_KEY = "worry:empathy:ranking";
    public static final String WORRY_RECENT_SORT_KEY = "worry:recent:sort";

    public static final String BLACK_LIST = "blacklist:";

    public static final String WORRY_COMMENT_ID_KEY = "worry:comment:id";
    public static final String WORRY_EMPATHY_USER_KEY_PREFIX = "worry:empathyUser:";

    public static final String WORRY_ID_SEQ_KEY = "worry:id:seq";
    public static final String WORRY_COMMENT_ID_SEQ_KEY = "worry:comment:id:seq";

    public static final String MAIN_PAGE_CACHE = "mainPageCache::main";
    public static final String ZONE_SUMMARY_TOTAL_COUNT = "zone:summary:total:count:";

    public static final String CHALLENGE_DETAIL_CACHE_PREFIX = "challenge:detail:";
    public static final String MOGAKZONE_DETAIL_CACHE_PREFIX = "mogakzone:detail:";

    public static final String CHALLENGE_DETAIL_LOCK = "lock:challenge:";
    public static final String MOGAKZONE_DETAIL_LOCK = "lock:zone:";

}