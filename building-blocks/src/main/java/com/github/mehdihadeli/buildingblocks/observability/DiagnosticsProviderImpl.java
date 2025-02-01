package com.github.mehdihadeli.buildingblocks.observability;

import com.github.mehdihadeli.buildingblocks.abstractions.observability.CreateSpanInfo;
import com.github.mehdihadeli.buildingblocks.abstractions.observability.DiagnosticsProvider;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiagnosticsProviderImpl implements DiagnosticsProvider {

    private final String instrumentationName;
    private final Tracer tracer;
    private final Meter meter;

    public DiagnosticsProviderImpl(Tracer tracer, Meter meter, ObservabilityProperties observabilityProperties) {
        this.instrumentationName = observabilityProperties.getInstrumentationName();
        this.tracer = tracer;
        this.meter = meter;
    }

    @Override
    public String getInstrumentationName() {
        return instrumentationName;
    }

    @Override
    public Tracer getTracer() {
        return tracer;
    }

    @Override
    public Meter getMeter() {
        return meter;
    }

    @Override
    public void executeSpan(CreateSpanInfo createSpanInfo, Consumer<Span> action) {
        String spanName = String.format("%s.%s", instrumentationName, createSpanInfo.getName());
        SpanBuilder spanBuilder = tracer.spanBuilder(spanName).setSpanKind(createSpanInfo.getActivityKind());

        SpanContext parent = createSpanInfo.getParent();
        if (parent != null) {
            spanBuilder.setParent(Context.root().with(Span.wrap(parent)));
        } else if (createSpanInfo.getParentId() != null) {
            spanBuilder.setNoParent();
        }

        // Add tags
        for (Map.Entry<String, Object> entry : createSpanInfo.getTags().entrySet()) {
            spanBuilder.setAttribute(entry.getKey(), entry.getValue().toString());
        }

        Span span = spanBuilder.startSpan();
        try (Scope scope = span.makeCurrent()) {
            action.accept(span);
            // Set the span status to "OK" if no exception is thrown
            OtelUtils.setOkStatus(span, "Operation completed successfully");
        } catch (Exception e) {
            // Set the span status to "Error" and record the exception
            OtelUtils.setErrorStatus(span, e, "Operation failed: " + e.getMessage());
            throw e; // Re-throw the exception after recording it
        } finally {
            span.end();
        }
    }

    @Override
    public <TResult> TResult executeSpan(CreateSpanInfo createSpanInfo, Function<Span, TResult> action) {
        // Create the span name using the format "{InstrumentationName}.{createSpanInfo.getName()}"
        String spanName = String.format("%s.%s", instrumentationName, createSpanInfo.getName());

        SpanBuilder spanBuilder = tracer.spanBuilder(spanName).setSpanKind(createSpanInfo.getActivityKind());

        SpanContext parent = createSpanInfo.getParent();
        if (parent != null) {
            spanBuilder.setParent(Context.root().with(Span.wrap(parent)));
        } else if (createSpanInfo.getParentId() != null) {
            spanBuilder.setNoParent();
        }

        // Add tags
        for (Map.Entry<String, Object> entry : createSpanInfo.getTags().entrySet()) {
            spanBuilder.setAttribute(entry.getKey(), entry.getValue().toString());
        }

        Span span = spanBuilder.startSpan();
        try (Scope scope = span.makeCurrent()) {
            TResult result = action.apply(span);
            // Set the span status to "OK" if no exception is thrown
            OtelUtils.setOkStatus(span, "Operation completed successfully");
            return result;
        } catch (Exception e) {
            // Set the span status to "Error" and record the exception
            OtelUtils.setErrorStatus(span, e, "Operation failed: " + e.getMessage());
            throw e; // Re-throw the exception after recording it
        } finally {
            span.end();
        }
    }
}