package com.github.mehdihadeli.buildingblocks.observability;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.*;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.semconv.ExceptionAttributes;
import io.opentelemetry.semconv.OtelAttributes;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

/**
 * utility class for creating and managing OpenTelemetry tracing, metrics, and logs.
 */
public final class OtelUtils {

    // Private constructor to prevent instantiation
    private OtelUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    /**
     * Creates a new span and executes the provided operation within the span context.
     *
     * @param tracer The Tracer instance.
     * @param spanName The name of the span.
     * @param operation The operation to execute within the span context.
     */
    public static void withSpan(Tracer tracer, String spanName, Consumer<Span> operation) {
        Span span = tracer.spanBuilder(spanName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            operation.accept(span);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Operation failed: " + e.getMessage());
            span.recordException(e);
            throw e; // Re-throw the exception after recording it
        } finally {
            span.end();
        }
    }

    /**
     * Creates a new span and executes the provided operation within the span context.
     * Returns the result of the operation.
     *
     * @param tracer The Tracer instance.
     * @param spanName The name of the span.
     * @param operation The operation to execute within the span context.
     * @param <T> The type of the result.
     * @return The result of the operation.
     */
    public static <T> T withSpan(Tracer tracer, String spanName, Function<Span, T> operation) {
        Span span = tracer.spanBuilder(spanName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return operation.apply(span);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Operation failed: " + e.getMessage());
            span.recordException(e);
            throw e; // Re-throw the exception after recording it
        } finally {
            span.end();
        }
    }

    /**
     * Creates and configures a SpanBuilder with the provided parameters.
     *
     * @param tracer The Tracer instance to create the SpanBuilder.
     * @param instrumentationName The instrumentation name (e.g., "MyApp").
     * @param spanName The name of the span (e.g., "ProcessRequest").
     * @param spanKind The kind of the span (e.g., SpanKind.INTERNAL).
     * @param parent The parent SpanContext, if any.
     * @param parentId The parent span ID, if any.
     * @param tags A map of tags to add to the span.
     * @return A configured SpanBuilder instance.
     */
    public static SpanBuilder createSpanBuilder(
            Tracer tracer,
            String instrumentationName,
            String spanName,
            SpanKind spanKind,
            SpanContext parent,
            String parentId,
            Map<String, Object> tags) {

        // Create the full span name using the format "{InstrumentationName}.{SpanName}"
        String fullSpanName = String.format("%s.%s", instrumentationName, spanName);

        // Create the SpanBuilder
        SpanBuilder spanBuilder = tracer.spanBuilder(fullSpanName).setSpanKind(spanKind);

        // Set the parent context, if provided
        if (parent != null) {
            spanBuilder.setParent(Context.root().with(Span.wrap(parent)));
        } else if (parentId != null) {
            spanBuilder.setNoParent();
        }

        // Add tags to the span
        return addDefaultTags(spanBuilder, tags);
    }

    /**
     * Adds default tags to the SpanBuilder (e.g., application name, version, environment).
     *
     * @param spanBuilder The SpanBuilder to configure.
     * @param defaultTags A map of default tags to add.
     * @return The updated SpanBuilder instance.
     */
    public static SpanBuilder addDefaultTags(SpanBuilder spanBuilder, Map<String, Object> defaultTags) {
        if (defaultTags != null) {
            for (Map.Entry<String, Object> entry : defaultTags.entrySet()) {
                spanBuilder.setAttribute(entry.getKey(), entry.getValue().toString());
            }
        }
        return spanBuilder;
    }

    /**
     * Adds baggage items (key-value pairs) to the SpanBuilder for context propagation.
     *
     * @param spanBuilder The SpanBuilder to configure.
     * @param baggageItems A map of baggage items to add.
     * @return The updated SpanBuilder instance.
     */
    public static SpanBuilder addBaggageItems(SpanBuilder spanBuilder, Map<String, String> baggageItems) {
        if (baggageItems != null) {
            for (Map.Entry<String, String> entry : baggageItems.entrySet()) {
                spanBuilder.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return spanBuilder;
    }

    /**
     * Adds an event to the span with a name and optional attributes.
     *
     * @param span The span to which the event will be added.
     * @param eventName The name of the event.
     * @param attributes A map of attributes for the event.
     */
    public static void addEvent(Span span, String eventName, Map<String, Object> attributes) {
        if (span != null && eventName != null) {
            Attributes eventAttributes = Attributes.empty();
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    eventAttributes = eventAttributes.toBuilder()
                            .put(entry.getKey(), entry.getValue().toString())
                            .build();
                }
            }
            span.addEvent(eventName, eventAttributes);
        }
    }

    /**
     * Records a counter metric with the given name and value.
     *
     * @param meter The Meter instance.
     * @param metricName The name of the metric.
     * @param value The value to record.
     * @param attributes Additional attributes for the metric.
     */
    public static void recordCounter(Meter meter, String metricName, long value, Attributes attributes) {
        LongCounter counter = meter.counterBuilder(metricName).build();
        counter.add(value, attributes);
    }

    /**
     * Records an UpDownCounter metric with the given name and value.
     *
     * @param meter The Meter instance.
     * @param metricName The name of the metric.
     * @param value The value to record.
     * @param attributes Additional attributes for the metric.
     */
    public static void recordUpDownCounter(Meter meter, String metricName, long value, Attributes attributes) {
        LongUpDownCounter upDownCounter = meter.upDownCounterBuilder(metricName).build();
        upDownCounter.add(value, attributes);
    }

    /**
     * Records a Histogram metric with the given name and value.
     *
     * @param meter The Meter instance.
     * @param metricName The name of the metric.
     * @param value The value to record.
     * @param attributes Additional attributes for the metric.
     */
    public static void recordHistogram(Meter meter, String metricName, double value, Attributes attributes) {
        DoubleHistogram histogram = meter.histogramBuilder(metricName).build();
        histogram.record(value, attributes);
    }

    /**
     * Records a Gauge metric with the given name and value.
     *
     * @param meter The Meter instance.
     * @param metricName The name of the metric.
     * @param value The value to record.
     * @param attributes Additional attributes for the metric.
     */
    public static void recordGauge(Meter meter, String metricName, double value, Attributes attributes) {
        ObservableDoubleMeasurement gauge = meter.gaugeBuilder(metricName).buildObserver();
        gauge.record(value, attributes);
    }

    /**
     * Logs a message with the given severity and attributes.
     *
     * @param logger The Logger instance.
     * @param severity The severity level of the log (e.g., "INFO", "ERROR").
     * @param message The log message.
     * @param attributes Additional attributes for the log.
     */
    public static void log(Logger logger, String severity, String message, Attributes attributes) {
        logger.logRecordBuilder()
                .setBody(message)
                .setSeverityText(severity)
                .setAllAttributes(attributes)
                .emit();
    }

    /**
     * Logs a message with the given severity.
     *
     * @param logger The Logger instance.
     * @param severity The severity level of the log (e.g., "INFO", "ERROR").
     * @param message The log message.
     */
    public static void log(Logger logger, String severity, String message) {
        log(logger, severity, message, Attributes.empty());
    }

    /**
     * Sets an "OK" status on the provided Span, indicating a successful operation.
     *
     * @param span The Span to update.
     * @param description An optional description of the successful operation.
     * @return The updated Span with the status and tags set.
     */
    public static Span setOkStatus(Span span, String description) {
        if (span == null) {
            throw new IllegalArgumentException("Span cannot be null.");
        }

        // Set the status of the span to "OK"
        span.setStatus(StatusCode.OK, description);

        // Add telemetry tags for status
        span.setAttribute(OtelAttributes.OTEL_STATUS_CODE, OtelAttributes.OtelStatusCodeValues.OK);
        if (!StringUtils.isEmpty(description) && !description.isEmpty()) {
            span.setAttribute(OtelAttributes.OTEL_STATUS_DESCRIPTION, description);
        }

        return span;
    }

    /**
     * Sets an "Unset" status on the provided Span, indicating no explicit status was applied.
     *
     * @param span The Span to update.
     * @param description An optional description of the unset status.
     * @return The updated Span with the status and tags set.
     */
    public static Span setUnsetStatus(Span span, String description) {
        if (span == null) {
            throw new IllegalArgumentException("Span cannot be null.");
        }

        // Set the status of the span to "Unset"
        span.setStatus(StatusCode.UNSET, description);

        // Add telemetry tags for status
        span.setAttribute(OtelAttributes.OTEL_STATUS_CODE, "UNSET");
        if (!StringUtils.isEmpty(description) && !description.isEmpty()) {
            span.setAttribute(OtelAttributes.OTEL_STATUS_DESCRIPTION, description);
        }

        return span;
    }

    /**
     * Sets an "Error" status on the provided Span, indicating a failed operation.
     *
     * @param span The Span to update.
     * @param exception The exception associated with the error, if available.
     * @param description An optional description of the error.
     * @return The updated Span with the status, error details, and tags set.
     */
    public static Span setErrorStatus(Span span, Exception exception, String description) {
        if (span == null) {
            throw new IllegalArgumentException("Span cannot be null.");
        }

        // Set the status of the span to "Error"
        span.setStatus(StatusCode.ERROR, description);

        // Add telemetry tags for status
        span.setAttribute(OtelAttributes.OTEL_STATUS_CODE, OtelAttributes.OtelStatusCodeValues.ERROR);
        if (!StringUtils.isEmpty(description) && !description.isEmpty()) {
            span.setAttribute(OtelAttributes.OTEL_STATUS_DESCRIPTION, description);
        }

        // Add detailed exception tags, if an exception is provided
        return setExceptionTags(span, exception);
    }

    /**
     * Adds exception-related tags to the provided Span.
     *
     * @param span The Span to update.
     * @param ex The exception to record.
     * @return The updated Span with the exception tags set.
     */
    public static Span setExceptionTags(Span span, Exception ex) {
        if (span == null || ex == null) {
            return span;
        }

        // Set the status of the span to "Error"
        span.setStatus(StatusCode.ERROR);

        // Record the exception
        span.recordException(ex);

        // Add exception-related tags
        span.setAttribute(ExceptionAttributes.EXCEPTION_MESSAGE, ex.getMessage());
        span.setAttribute(ExceptionAttributes.EXCEPTION_STACKTRACE, ex.toString());
        span.setAttribute(ExceptionAttributes.EXCEPTION_TYPE, ex.getClass().getName());

        return span;
    }

    /**
     * Adds metadata from a map to a Span as attributes.
     *
     * @param metadata The map containing the metadata.
     * @param span The Span to which the metadata should be added.
     */
    public static void addMetadataAsTags(Map<String, Object> metadata, Span span) {
        notBeNull(metadata,"metadata");
        notBeNull(span,"span");

        metadata.forEach((key, value) -> span.setAttribute(key, value.toString()));
    }

    /**
     * Merges multiple collections of tags into one. If the same key exists in multiple collections,
     * the value from the last collection will override the earlier ones.
     *
     * @param tagCollections A list of maps containing tags.
     * @return A merged map of tags.
     */
    public static Map<String, String> mergeTags(List<Map<String, String>> tagCollections) {
        if (tagCollections == null) {
            throw new IllegalArgumentException("Tag collections cannot be null");
        }

        Map<String, String> mergedTags = new HashMap<>();
        for (Map<String, String> tags : tagCollections) {
            if (tags != null) {
                mergedTags.putAll(tags);
            }
        }

        return Collections.unmodifiableMap(mergedTags);
    }
}