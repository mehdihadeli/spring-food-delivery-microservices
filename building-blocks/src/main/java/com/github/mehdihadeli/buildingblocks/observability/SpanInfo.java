package com.github.mehdihadeli.buildingblocks.observability;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpanInfo {
    private String name;
    private Instant startTime;
    private Duration duration;
    private String status;
    private String statusDescription;
    private Map<String, String> tags = new HashMap<>();
    private List<SpanEventInfo> events;
    private String traceId;
    private String spanId;
    private String parentId;
    private SpanContext parent;
    private SpanKind kind;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public List<SpanEventInfo> getEvents() {
        return events;
    }

    public void setEvents(List<SpanEventInfo> events) {
        this.events = events;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public SpanContext getParent() {
        return parent;
    }

    public void setParent(SpanContext parent) {
        this.parent = parent;
    }

    public SpanKind getKind() {
        return kind;
    }

    public void setKind(SpanKind kind) {
        this.kind = kind;
    }
}
