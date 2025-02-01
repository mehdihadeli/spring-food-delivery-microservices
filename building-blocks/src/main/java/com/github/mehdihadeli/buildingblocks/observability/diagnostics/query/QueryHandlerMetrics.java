package com.github.mehdihadeli.buildingblocks.observability.diagnostics.query;

import com.github.mehdihadeli.buildingblocks.observability.TelemetryTags;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;

import java.util.concurrent.TimeUnit;

public class QueryHandlerMetrics {

    private final LongUpDownCounter activeQueriesCounter;
    private final LongCounter totalQueriesNumber;
    private final LongCounter successQueriesNumber;
    private final LongCounter failedQueriesNumber;
    private final DoubleHistogram handlerDuration;

    private long startTime;

    public QueryHandlerMetrics(Meter meter) {
        this.activeQueriesCounter = meter.upDownCounterBuilder("application.queries.active_count")
                .setDescription("Number of queries currently being handled")
                .setUnit("{active_queries}")
                .build();

        this.totalQueriesNumber = meter.counterBuilder("application.queries.total_executed_count")
                .setDescription("Total number of executed queries that sent to query handlers")
                .setUnit("{total_queries}")
                .build();

        this.successQueriesNumber = meter.counterBuilder("application.queries.success_count")
                .setDescription("Number of queries that handled successfully")
                .setUnit("{success_queries}")
                .build();

        this.failedQueriesNumber = meter.counterBuilder("application.queries.failed_count")
                .setDescription("Number of queries that handled with errors")
                .setUnit("{failed_queries}")
                .build();

        this.handlerDuration = meter.histogramBuilder("application.queries.handler_duration")
                .setDescription("Measures the duration of query handler")
                .setUnit("s")
                .build();
    }

    public <TQuery> void startExecuting(Class<TQuery> queryClass) {
        String queryName = queryClass.getSimpleName();
        String queryType = queryClass.getName();
        String queryHandlerName = getQueryHandlerName(queryClass);
        String queryHandlerType = getQueryHandlerType(queryClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Queries.QUERY, queryName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_TYPE, queryType)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER, queryHandlerName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER_TYPE, queryHandlerType)
                .build();

        activeQueriesCounter.add(1, tags);
        totalQueriesNumber.add(1, tags);

        startTime = System.nanoTime();
    }

    public <TQuery> void finishExecuting(Class<TQuery> queryClass) {
        String queryName = queryClass.getSimpleName();
        String queryType = queryClass.getName();
        String queryHandlerName = getQueryHandlerName(queryClass);
        String queryHandlerType = getQueryHandlerType(queryClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Queries.QUERY, queryName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_TYPE, queryType)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER, queryHandlerName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER_TYPE, queryHandlerType)
                .build();

        activeQueriesCounter.add(-1, tags);

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedTime);

        handlerDuration.record(elapsedTimeSeconds, tags);
        successQueriesNumber.add(1, tags);
    }

    public <TQuery> void failedQuery(Class<TQuery> queryClass) {
        String queryName = queryClass.getSimpleName();
        String queryType = queryClass.getName();
        String queryHandlerName = getQueryHandlerName(queryClass);
        String queryHandlerType = getQueryHandlerType(queryClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Queries.QUERY, queryName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_TYPE, queryType)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER, queryHandlerName)
                .put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER_TYPE, queryHandlerType)
                .build();

        failedQueriesNumber.add(1, tags);
    }

    /**
     * Gets the name of the query handler.
     *
     * @param queryClass The query class.
     * @return The name of the query handler.
     */
    private <TQuery> String getQueryHandlerName(Class<TQuery> queryClass) {
        return queryClass.getEnclosingClass() != null
                ? queryClass.getEnclosingClass().getSimpleName()
                : queryClass.getSimpleName();
    }

    /**
     * Gets the fully qualified name of the query handler type.
     *
     * @param queryClass The query class.
     * @return The fully qualified name of the query handler type.
     */
    private <TQuery> String getQueryHandlerType(Class<TQuery> queryClass) {
        return queryClass.getEnclosingClass() != null
                ? queryClass.getEnclosingClass().getName()
                : queryClass.getName();
    }
}