package com.hnc.mogak.global.monitoring;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class QueryCountInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        RequestContext ctx = RequestContextHolder.getContext();

        if (ctx != null) {
            ctx.incrementQueryCount(sql);
        }

        return sql;
    }
}
