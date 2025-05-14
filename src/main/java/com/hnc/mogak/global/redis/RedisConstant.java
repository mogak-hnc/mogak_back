package com.hnc.mogak.global.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstant {

    public static final String ZONE_PARTICIPANT_COUNT = "zoneParticipantCount";

    public static final String WORRY_EMPATHY_RANKING_KEY = "worry:empathy:ranking";
    public static final String WORRY_RECENT_SORT_KEY = "worry:recent:sort";

    public static final String BLACK_LIST = "blacklist:";

    public static final String WORRY_ID_KEY = "worry:id:";
    public static final String WORRY_COMMENT_ID_KEY = "worry:comment:id";
    public static final String WORRY_EMPATHY_USER_KEY_PREFIX = "worry:empathyUser:";

}