package com.github.mehdihadeli.buildingblocks.observability.diagnostics.query;

import io.opentelemetry.api.trace.Span;
import java.util.function.Function;

public interface QueryHandlerSpan {
    <TQuery, TResult> TResult execute(Class<TQuery> queryClass, Function<Span, TResult> action);
}
