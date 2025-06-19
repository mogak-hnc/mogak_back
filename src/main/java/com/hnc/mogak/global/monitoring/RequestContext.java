package com.hnc.mogak.global.monitoring;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RequestContext {

    private final String httpMethod;
    private final String bestMatchPath;
    private final String uuid;

    private final Map<QueryType, Integer> queryCountByType = new HashMap<>();

    @Builder
    public RequestContext(String httpMethod, String bestMatchPath) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.httpMethod = httpMethod;
        this.bestMatchPath = bestMatchPath;
    }

    public void incrementQueryCount(String sql) {
        QueryType queryType = QueryType.from(sql);
        queryCountByType.put(queryType, queryCountByType.getOrDefault(queryType, 0) + 1);
    }

}
