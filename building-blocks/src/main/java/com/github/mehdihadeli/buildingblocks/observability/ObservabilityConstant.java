package com.github.mehdihadeli.buildingblocks.observability;

public class ObservabilityConstant {
    public static String instrumentationName;

    public static class Components {
        public static final String COMMAND_HANDLER = "CommandHandler";
        public static final String QUERY_HANDLER = "QueryHandler";
        public static final String EVENT_STORE = "EventStore";
        public static final String PRODUCER = "Producer";
        public static final String CONSUMER = "Consumer";
        public static final String EVENT_HANDLER = "EventHandler";
    }
}
