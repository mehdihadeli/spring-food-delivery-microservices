package com.github.mehdihadeli.buildingblocks.core.messaging;

public final class MessageHeaders {
    // Private constructor to prevent instantiation
    private MessageHeaders() {}

    public static final String MESSAGE_ID = "message-id";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String CAUSATION_ID = "causation-id";
    public static final String TRACE_ID = "trace-id";
    public static final String SPAN_ID = "span-id";
    public static final String PARENT_SPAN_ID = "parent-id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String CREATED = "created";
    public static final String EXCHANGE_OR_TOPIC = "exchange-topic";
    public static final String QUEUE = "queue";
}
