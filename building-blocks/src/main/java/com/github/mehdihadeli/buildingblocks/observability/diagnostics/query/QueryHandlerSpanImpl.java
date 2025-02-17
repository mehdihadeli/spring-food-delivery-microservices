package com.github.mehdihadeli.buildingblocks.observability.diagnostics.query;

import com.github.mehdihadeli.buildingblocks.abstractions.observability.CreateSpanInfo;
import com.github.mehdihadeli.buildingblocks.abstractions.observability.DiagnosticsProvider;
import com.github.mehdihadeli.buildingblocks.observability.ObservabilityConstant;
import com.github.mehdihadeli.buildingblocks.observability.TelemetryTags;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QueryHandlerSpanImpl implements QueryHandlerSpan {
    private final DiagnosticsProvider diagnosticsProvider;

    public QueryHandlerSpanImpl(DiagnosticsProvider diagnosticsProvider) {
        this.diagnosticsProvider = diagnosticsProvider;
    }

    @Override
    public <TQuery, TResult> TResult execute(Class<TQuery> queryClass, Function<Span, TResult> action) {
        String queryName = queryClass.getSimpleName();
        String queryFullName = queryClass.getName();
        String queryHandlerName = getQueryHandlerName(queryClass);
        String queryHandlerFullName = getQueryHandlerType(queryClass);

        // Create the activity name
        String spanName =
                String.format("%s.%s/%s", ObservabilityConstant.Components.QUERY_HANDLER, queryHandlerName, queryName);

        CreateSpanInfo activityInfo = new CreateSpanInfo();
        activityInfo.setName(spanName);
        activityInfo.setActivityKind(SpanKind.CONSUMER);

        // Add tags
        Map<String, Object> tags = new HashMap<>();
        tags.put(TelemetryTags.Tracing.Application.Queries.QUERY, queryName);
        tags.put(TelemetryTags.Tracing.Application.Queries.QUERY_TYPE, queryFullName);
        tags.put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER, queryHandlerName);
        tags.put(TelemetryTags.Tracing.Application.Queries.QUERY_HANDLER_TYPE, queryHandlerFullName);
        activityInfo.setTags(tags);

        return diagnosticsProvider.executeSpan(activityInfo, action);
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
