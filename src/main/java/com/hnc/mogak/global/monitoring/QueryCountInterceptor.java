package com.hnc.mogak.global.monitoring;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueryCountInterceptor implements HandlerInterceptor {

    public static final String UNKNOWN_PATH = "UNKNOWN_PATH";

    private final MeterRegistry meterRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String httpMethod = request.getMethod(); // GET, POST, PUT, DELETE, OPTIONS, HEAD

        String bestMatchPath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // /api/mogak/challenge/1 -> /api/mogak/challenge/{challengeId}
        if (bestMatchPath == null) {
            bestMatchPath = UNKNOWN_PATH;
        }

        RequestContext ctx = RequestContext.builder()
                .httpMethod(httpMethod)
                .bestMatchPath(bestMatchPath)
                .build();

        RequestContextHolder.initContext(ctx);

        log.info("[{}] Request Method=[{}] URL=[{}]", ctx.getUuid(), request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestContext ctx = RequestContextHolder.getContext();

        if (ctx != null) {
            Map<QueryType, Integer> queryCountByType = ctx.getQueryCountByType();
            queryCountByType.forEach((type, count) -> log.info(">>>>> QueryType={}, Count={}", type, count));
            queryCountByType.forEach((queryType, count) -> increment(ctx, queryType, count));
        }
        try {
            log.info("[{}] Response Status=[{}]", ctx.getUuid(), response.getStatus());
        } catch (NullPointerException e) {
            log.warn("UUID에 NULL 들어감");
        }
        RequestContextHolder.clear();
        log.info("increment 로직 끝");
    }
    
    private void increment(RequestContext ctx, QueryType queryType, Integer count) {
        log.info("increment 로직 시작");
        DistributionSummary summary = DistributionSummary.builder("mogak.query.per_request")
                .description("Number of SQL queries per request")
                .tag("path", ctx.getBestMatchPath())
                .tag("http_method", ctx.getHttpMethod())
                .tag("query_type", queryType.name())
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        summary.record(count);
    }

}
