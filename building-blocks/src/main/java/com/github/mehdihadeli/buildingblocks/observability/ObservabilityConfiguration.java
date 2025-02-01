package com.github.mehdihadeli.buildingblocks.observability;

import com.github.mehdihadeli.buildingblocks.abstractions.observability.DiagnosticsProvider;
import com.github.mehdihadeli.buildingblocks.core.utils.EnvironmentUtils;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerSpan;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerSpan;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.instrumentation.spring.autoconfigure.internal.properties.OtelSpringProperties;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_NAME;
import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_VERSION;

// https://github.com/open-telemetry/opentelemetry-java-examples
// https://opentelemetry.io/docs/languages/java/sdk/
// https://opentelemetry.io/docs/languages/java/api/

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ObservabilityProperties.class)
public class ObservabilityConfiguration {

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry, ObservabilityProperties observabilityProperties) {
        // the instrumentation name (also referred to as the instrumentation scope) is used to identify the library or
        // component that is generating telemetry data (traces, metrics, or logs).
        var instrumentationName = notBeEmpty(observabilityProperties.getInstrumentationName(), "instrumentationName");
        // Create a Tracer with the custom instrumentation name for the application or library
        return openTelemetry.getTracer(instrumentationName);
    }

    @Bean
    public Meter meter(OpenTelemetry openTelemetry, ObservabilityProperties observabilityProperties) {
        // the instrumentation name (also referred to as the instrumentation scope) is used to identify the library or
        // component that is generating telemetry data (traces, metrics, or logs).
        var instrumentationName = notBeEmpty(observabilityProperties.getInstrumentationName(), "instrumentationName");
        // Create a Meter with the custom instrumentation name for the application or library
        return openTelemetry.getMeter(instrumentationName);
    }

    @Bean
    public DiagnosticsProvider diagnosticsProvider(
            Tracer tracer, Meter meter, ObservabilityProperties observabilityProperties) {
        return new DiagnosticsProviderImpl(tracer, meter, observabilityProperties);
    }

    @Bean
    public CommandHandlerSpan commandHandlerActivity(DiagnosticsProvider diagnosticsProvider) {
        return new CommandHandlerSpan(diagnosticsProvider);
    }

    @Bean
    public QueryHandlerSpan queryHandlerActivity(DiagnosticsProvider diagnosticsProvider) {
        return new QueryHandlerSpan(diagnosticsProvider);
    }

    @Bean
    public QueryHandlerMetrics queryHandlerMetrics(Meter meter) {
        return new QueryHandlerMetrics(meter);
    }

    @Bean
    public CommandHandlerMetrics commandHandlerMetrics(Meter meter) {
        return new CommandHandlerMetrics(meter);
    }

    // https://opentelemetry.io/docs/zero-code/java/spring-boot-starter/sdk-configuration/#programmatic-configuration
    @Bean
    public AutoConfigurationCustomizerProvider autoConfigurationCustomizerProvider(
            ObservabilityProperties observabilityProperties,
            OtelSpringProperties otelSpringProperties,
            Environment env) {
        // Include required resource attribute on all spans and metrics
        Resource resource = Resource.getDefault()
                .merge(Resource.builder()
                        .put(SERVICE_NAME, observabilityProperties.getServiceName())
                        .put(SERVICE_VERSION, "1.0.")
                        .build());

        return builder -> {
            // .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()));

            // Configure resource
            builder.addResourceCustomizer((resourceDefault, config) -> resourceDefault.merge(resource));

            // Configure propagators (W3C Trace Context)
            builder.addPropagatorCustomizer((propagators, config) -> W3CTraceContextPropagator.getInstance());

            // configure tracing
            builder.addTracerProviderCustomizer((tracerProviderBuilder, configProperties) -> {
                tracerProviderBuilder.setResource(resource);
                configureTracingExporters(tracerProviderBuilder, observabilityProperties);
                if (EnvironmentUtils.isDev(env)) {
                    tracerProviderBuilder.setSampler(Sampler.alwaysOn());
                }

                return tracerProviderBuilder;
            });

            // configure metrics
            builder.addMeterProviderCustomizer(
                    (SdkMeterProviderBuilder meterProviderBuilder, ConfigProperties config) -> {
                        meterProviderBuilder.setResource(resource);
                        configMetricsExporters(meterProviderBuilder, observabilityProperties);

                        return meterProviderBuilder;
                    });

            // config logs
            builder.addLoggerProviderCustomizer((loggerProviderBuilder, configProperties) -> {
                loggerProviderBuilder.setResource(resource);
                configLogsExporters(loggerProviderBuilder, observabilityProperties);

                return loggerProviderBuilder;
            });
        };
    }

    private void configLogsExporters(
            SdkLoggerProviderBuilder loggerProviderBuilder, ObservabilityProperties observabilityProperties) {

        if (observabilityProperties.isUseOtlpGrpcExporter()) {
            // Create an OTLP gRPC Log Exporter
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/log-appender/src/main/java/io/opentelemetry/example/logappender/Application.java
            OtlpGrpcLogRecordExporter otlpGrpcLogRecordExporter = OtlpGrpcLogRecordExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpGrpcEndpoint()) // OTLP gRPC endpoint
                    .build();

            loggerProviderBuilder.addLogRecordProcessor(
                    BatchLogRecordProcessor.builder(otlpGrpcLogRecordExporter).build());
        }
        if (observabilityProperties.isUseOtlpHttpExporter()) {
            // Create an OTLP http Log Exporter
            OtlpHttpLogRecordExporter otlpHttpLogRecordExporter = OtlpHttpLogRecordExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpHttpEndpoint()) // OTLP Http endpoint
                    .build();

            loggerProviderBuilder.addLogRecordProcessor(
                    BatchLogRecordProcessor.builder(otlpHttpLogRecordExporter).build());
        }

        if (observabilityProperties.isUseAspireOtlpExporter()) {
            if (!StringUtils.isEmpty(
                    observabilityProperties.getAspireDashboardOtlpOptions().getOtlpGrpcEndpoint())) {
                OtlpGrpcLogRecordExporter aspireOtlpGrpcLogRecordExporter = OtlpGrpcLogRecordExporter.builder()
                        .setEndpoint(observabilityProperties
                                .getAspireDashboardOtlpOptions()
                                .getOtlpGrpcEndpoint()) // OTLP gRPC endpoint
                        .build();

                loggerProviderBuilder.addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(aspireOtlpGrpcLogRecordExporter)
                                .build());
            }
        }

        if (observabilityProperties.isUseConsoleExporter()) {
            // https://github.com/open-telemetry/opentelemetry-java/blob/main/exporters/logging/src/main/java/io/opentelemetry/exporter/logging/SystemOutLogRecordExporter.java
            // Create a custom Console Log Exporter
            SystemOutLogRecordExporter consoleLogExporter = SystemOutLogRecordExporter.create();

            // Configure the SDK to use the custom Console Log Exporter
            loggerProviderBuilder.addLogRecordProcessor(SimpleLogRecordProcessor.create(consoleLogExporter));
        }
    }

    private void configMetricsExporters(
            SdkMeterProviderBuilder meterProviderBuilder, ObservabilityProperties observabilityProperties) {
        if (observabilityProperties.isUsePrometheusExporter()) {
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/prometheus/src/main/java/io/opentelemetry/example/prometheus/ExampleConfiguration.java
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/prometheus/src/main/java/io/opentelemetry/exporter/prometheus
            // Create a Prometheus HTTP server exporter
            // for exporting app metrics to `/metrics` endpoint
            MetricReader prometheusReader = PrometheusHttpServer.builder()
                    .setPort(observabilityProperties.getPrometheusPort())
                    .build();

            meterProviderBuilder.registerMetricReader(prometheusReader);
        }

        if (observabilityProperties.isUseConsoleExporter()) {
            // write metrics to logs
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/logging
            // https://github.com/open-telemetry/opentelemetry-java/blob/main/exporters/logging/src/main/java/io/opentelemetry/exporter/logging/LoggingMetricExporter.java
            // Create a Logging Metric Exporter
            LoggingMetricExporter loggingMetricExporter = LoggingMetricExporter.create();

            // Configure the SDK to use the Logging Metric Exporter
            meterProviderBuilder.registerMetricReader(PeriodicMetricReader.builder(loggingMetricExporter)
                    .setInterval(5, java.util.concurrent.TimeUnit.SECONDS) // Export interval
                    .build());
        }

        if (observabilityProperties.isUseOtlpGrpcExporter()) {
            // Create an OTLP gRPC metric exporter
            OtlpGrpcMetricExporter otlpGrpcMetricExporter = OtlpGrpcMetricExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpGrpcEndpoint()) // OTLP gRPC endpoint
                    .build();
            meterProviderBuilder.registerMetricReader(PeriodicMetricReader.builder(otlpGrpcMetricExporter)
                    .setInterval(5, java.util.concurrent.TimeUnit.SECONDS) // Export interval
                    .build());
        }
        if (observabilityProperties.isUseOtlpHttpExporter()) {
            // Create an OTLP http metric exporter
            OtlpHttpMetricExporter otlpHttpMetricExporter = OtlpHttpMetricExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpHttpEndpoint()) // OTLP Http endpoint
                    .build();
            meterProviderBuilder.registerMetricReader(PeriodicMetricReader.builder(otlpHttpMetricExporter)
                    .setInterval(5, java.util.concurrent.TimeUnit.SECONDS) // Export interval
                    .build());
        }

        if (observabilityProperties.isUseAspireOtlpExporter()) {
            // Create an OTLP gRPC metric exporter
            if (!StringUtils.isEmpty(
                    observabilityProperties.getAspireDashboardOtlpOptions().getOtlpGrpcEndpoint())) {
                OtlpGrpcMetricExporter aspireOtlpGrpcMetricExporter = OtlpGrpcMetricExporter.builder()
                        .setEndpoint(observabilityProperties
                                .getAspireDashboardOtlpOptions()
                                .getOtlpGrpcEndpoint()) // OTLP gRPC endpoint
                        .build();
                meterProviderBuilder.registerMetricReader(PeriodicMetricReader.builder(aspireOtlpGrpcMetricExporter)
                        .setInterval(5, java.util.concurrent.TimeUnit.SECONDS) // Export interval
                        .build());
            }
        }
    }

    private static void configureTracingExporters(
            SdkTracerProviderBuilder tracerProviderBuilder, ObservabilityProperties observabilityProperties) {
        // Create an OTLP gRPC trace exporter
        if (observabilityProperties.isUseJaegerExporter()) {
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/otlp
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/jaeger/src/main/java/io/opentelemetry/example/jaeger/ExampleConfiguration.java
            OtlpGrpcSpanExporter jaegerOtlpExporter = OtlpGrpcSpanExporter.builder()
                    .setEndpoint(observabilityProperties.getJaegerOptions().getOtlpGrpcEndpoint())
                    .setTimeout(30, TimeUnit.SECONDS)
                    .build();
            tracerProviderBuilder.addSpanProcessor(
                    BatchSpanProcessor.builder(jaegerOtlpExporter).build());
        }

        if (observabilityProperties.isUseZipkinExporter()) {
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/zipkin/src/main/java/io/opentelemetry/example/zipkin/ExampleConfiguration.java
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/zipkin
            ZipkinSpanExporter zipkinExporter = ZipkinSpanExporter.builder()
                    .setEndpoint(observabilityProperties.getZipkinOptions().getHttpEndpoint())
                    .build();
            tracerProviderBuilder.addSpanProcessor(
                    SimpleSpanProcessor.builder(zipkinExporter).build());
        }

        if (observabilityProperties.isUseConsoleExporter()) {
            // write spans to logs
            // https://github.com/open-telemetry/opentelemetry-java/blob/main/exporters/logging/src/main/java/io/opentelemetry/exporter/logging/LoggingSpanExporter.java
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/logging
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/http/src/main/java/io/opentelemetry/example/http/ExampleConfiguration.java
            LoggingSpanExporter loggingSpanExporter = LoggingSpanExporter.create();

            tracerProviderBuilder.addSpanProcessor(SimpleSpanProcessor.create(loggingSpanExporter));
        }

        if (observabilityProperties.isUseOtlpGrpcExporter()) {
            // Create an OTLP gRPC trace exporter
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/otlp/src/main/java/io/opentelemetry/example/otlp/ExampleConfiguration.java
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/otlp
            OtlpGrpcSpanExporter otlpGrpcExporter = OtlpGrpcSpanExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpGrpcEndpoint())
                    .setTimeout(30, TimeUnit.SECONDS)
                    .build();
            tracerProviderBuilder.addSpanProcessor(
                    BatchSpanProcessor.builder(otlpGrpcExporter).build());
        }
        if (observabilityProperties.isUseOtlpHttpExporter()) {
            // Create an OTLP http trace exporter
            // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/otlp/src/main/java/io/opentelemetry/example/otlp/ExampleConfiguration.java
            // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/otlp
            OtlpHttpSpanExporter otlpHttpExporter = OtlpHttpSpanExporter.builder()
                    .setEndpoint(observabilityProperties.getOtlpOptions().getOtlpHttpEndpoint())
                    .setTimeout(30, TimeUnit.SECONDS)
                    .build();
            tracerProviderBuilder.addSpanProcessor(
                    BatchSpanProcessor.builder(otlpHttpExporter).build());
        }

        if (observabilityProperties.isUseAspireOtlpExporter()) {
            if (!StringUtils.isEmpty(
                    observabilityProperties.getAspireDashboardOtlpOptions().getOtlpGrpcEndpoint())) {
                // https://github.com/open-telemetry/opentelemetry-java-examples/blob/main/otlp/src/main/java/io/opentelemetry/example/otlp/ExampleConfiguration.java
                // https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/otlp
                OtlpGrpcSpanExporter aspireOtlpGrpcExporter = OtlpGrpcSpanExporter.builder()
                        .setEndpoint(observabilityProperties
                                .getAspireDashboardOtlpOptions()
                                .getOtlpGrpcEndpoint())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .build();
                tracerProviderBuilder.addSpanProcessor(
                        BatchSpanProcessor.builder(aspireOtlpGrpcExporter).build());
            }
        }
    }
}
