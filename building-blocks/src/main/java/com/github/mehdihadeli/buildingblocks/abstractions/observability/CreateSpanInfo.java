package com.github.mehdihadeli.buildingblocks.abstractions.observability;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;

import java.util.HashMap;
import java.util.Map;

public class CreateSpanInfo {
    private String name;
    private Map<String, Object> tags = new HashMap<>();
    private String parentId;
    private SpanContext parent;
    private SpanKind activityKind = SpanKind.INTERNAL;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
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

    public SpanKind getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(SpanKind activityKind) {
        this.activityKind = activityKind;
    }
}
