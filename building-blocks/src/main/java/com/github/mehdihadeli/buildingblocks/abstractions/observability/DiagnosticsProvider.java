package com.github.mehdihadeli.buildingblocks.abstractions.observability;

import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import java.util.function.Consumer;
import java.util.function.Function;

public interface DiagnosticsProvider {

    /**
     * Gets the instrumentation name.
     *
     * @return The instrumentation name.
     */
    String getInstrumentationName();

    /**
     * Gets the Meter.
     * Meter object can create metrics for instrumenting metrics in the application
     *
     * @return The Meter instance.
     */
    Meter getMeter();

    /**
     * Gets the Tracer.
     * Tracer object can create spans for instrumenting traces in the application
     *
     * @return The Tracer instance.
     */
    Tracer getTracer();

    /**
     * Executes an operation within a new span.
     *
     * @param createSpanInfo Information to create the span.
     * @param action The operation to execute within the span context.
     */
    void executeSpan(CreateSpanInfo createSpanInfo, Consumer<Span> action);

    /**
     * Executes an operation within a new span and returns a result.
     *
     * @param createSpanInfo Information to create the span.
     * @param action The operation to execute within the span context.
     * @param <TResult> The type of the result.
     * @return The result of the operation.
     */
    <TResult> TResult executeSpan(CreateSpanInfo createSpanInfo, Function<Span, TResult> action);
}
