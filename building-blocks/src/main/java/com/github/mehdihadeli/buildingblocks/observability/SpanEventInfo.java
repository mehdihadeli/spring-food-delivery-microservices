package com.github.mehdihadeli.buildingblocks.observability;

import java.time.Instant;
import java.util.Map;

public class SpanEventInfo {
    private String name;
    private Instant timestamp;
    private Map<String, Object> attributes;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
