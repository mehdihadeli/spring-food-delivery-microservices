package com.github.mehdihadeli.buildingblocks.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "otel")
public class ObservabilityProperties {

    // General Observability Settings
    private String instrumentationName;
    private String serviceName;
    private boolean metricsEnabled = true;
    private boolean tracingEnabled = true;
    private boolean loggingEnabled = true;

    // Exporter Flags
    private boolean usePrometheusExporter = true;
    private int prometheusPort = 9464;
    private boolean useOtlpGrpcExporter = true;
    private boolean useOtlpHttpExporter = false;
    private boolean useAspireOtlpExporter = true;
    private boolean useConsoleExporter;
    private boolean useJaegerExporter;
    private boolean useZipkinExporter;

    // Exporter Configuration Options
    private JaegerOptions jaegerOptions = new JaegerOptions();
    private ZipkinOptions zipkinOptions = new ZipkinOptions();
    private OtlpOptions otlpOptions = new OtlpOptions();
    private AspireDashboardOtlpOptions aspireDashboardOtlpOptions = new AspireDashboardOtlpOptions();

    // Getters and Setters
    public String getInstrumentationName() {
        return instrumentationName;
    }

    public void setInstrumentationName(String instrumentationName) {
        this.instrumentationName = instrumentationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public boolean isTracingEnabled() {
        return tracingEnabled;
    }

    public void setTracingEnabled(boolean tracingEnabled) {
        this.tracingEnabled = tracingEnabled;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public boolean isUsePrometheusExporter() {
        return usePrometheusExporter;
    }

    public void setUsePrometheusExporter(boolean usePrometheusExporter) {
        this.usePrometheusExporter = usePrometheusExporter;
    }

    public boolean isUseOtlpGrpcExporter() {
        return useOtlpGrpcExporter;
    }

    public void setUseOtlpGrpcExporter(boolean useOtlpGrpcExporter) {
        this.useOtlpGrpcExporter = useOtlpGrpcExporter;
    }

    public boolean isUseAspireOtlpExporter() {
        return useAspireOtlpExporter;
    }

    public void setUseAspireOtlpExporter(boolean useAspireOtlpExporter) {
        this.useAspireOtlpExporter = useAspireOtlpExporter;
    }

    public boolean isUseConsoleExporter() {
        return useConsoleExporter;
    }

    public void setUseConsoleExporter(boolean useConsoleExporter) {
        this.useConsoleExporter = useConsoleExporter;
    }

    public boolean isUseJaegerExporter() {
        return useJaegerExporter;
    }

    public void setUseJaegerExporter(boolean useJaegerExporter) {
        this.useJaegerExporter = useJaegerExporter;
    }

    public boolean isUseZipkinExporter() {
        return useZipkinExporter;
    }

    public void setUseZipkinExporter(boolean useZipkinExporter) {
        this.useZipkinExporter = useZipkinExporter;
    }

    public JaegerOptions getJaegerOptions() {
        return jaegerOptions;
    }

    public void setJaegerOptions(JaegerOptions jaegerOptions) {
        this.jaegerOptions = jaegerOptions;
    }

    public ZipkinOptions getZipkinOptions() {
        return zipkinOptions;
    }

    public void setZipkinOptions(ZipkinOptions zipkinOptions) {
        this.zipkinOptions = zipkinOptions;
    }

    public OtlpOptions getOtlpOptions() {
        return otlpOptions;
    }

    public void setOtlpOptions(OtlpOptions otlpOptions) {
        this.otlpOptions = otlpOptions;
    }

    public AspireDashboardOtlpOptions getAspireDashboardOtlpOptions() {
        return aspireDashboardOtlpOptions;
    }

    public void setAspireDashboardOtlpOptions(AspireDashboardOtlpOptions aspireDashboardOtlpOptions) {
        this.aspireDashboardOtlpOptions = aspireDashboardOtlpOptions;
    }

    public int getPrometheusPort() {
        return prometheusPort;
    }

    public void setPrometheusPort(int prometheusPort) {
        this.prometheusPort = prometheusPort;
    }

    public boolean isUseOtlpHttpExporter() {
        return useOtlpHttpExporter;
    }

    public void setUseOtlpHttpExporter(boolean useOtlpHttpExporter) {
        this.useOtlpHttpExporter = useOtlpHttpExporter;
    }

    // Nested Configuration Classes
    public static class JaegerOptions {
        private String otlpGrpcEndpoint = "http://localhost:4317";
        private String httpEndpoint = "http://localhost:14268/api/traces";

        public String getOtlpGrpcEndpoint() {
            return otlpGrpcEndpoint;
        }

        public void setOtlpGrpcEndpoint(String otlpGrpcEndpoint) {
            this.otlpGrpcEndpoint = otlpGrpcEndpoint;
        }

        public String getHttpEndpoint() {
            return httpEndpoint;
        }

        public void setHttpEndpoint(String httpEndpoint) {
            this.httpEndpoint = httpEndpoint;
        }
    }

    public static class ZipkinOptions {
        private String httpEndpoint = "http://localhost:9411/api/v2/spans";

        public String getHttpEndpoint() {
            return httpEndpoint;
        }

        public void setHttpEndpoint(String httpEndpoint) {
            this.httpEndpoint = httpEndpoint;
        }
    }

    public static class OtlpOptions {
        private String otlpGrpcEndpoint = "http://localhost:4317";
        private String otlpHttpEndpoint = "http://localhost:4318";

        public String getOtlpGrpcEndpoint() {
            return otlpGrpcEndpoint;
        }

        public void setOtlpGrpcEndpoint(String otlpGrpcEndpoint) {
            this.otlpGrpcEndpoint = otlpGrpcEndpoint;
        }

        public String getOtlpHttpEndpoint() {
            return otlpHttpEndpoint;
        }

        public void setOtlpHttpEndpoint(String otlpHttpEndpoint) {
            this.otlpHttpEndpoint = otlpHttpEndpoint;
        }
    }

    public static class AspireDashboardOtlpOptions {
        private String otlpGrpcEndpoint = "http://localhost:4319";

        public String getOtlpGrpcEndpoint() {
            return otlpGrpcEndpoint;
        }

        public void setOtlpGrpcEndpoint(String otlpGrpcEndpoint) {
            this.otlpGrpcEndpoint = otlpGrpcEndpoint;
        }
    }
}
