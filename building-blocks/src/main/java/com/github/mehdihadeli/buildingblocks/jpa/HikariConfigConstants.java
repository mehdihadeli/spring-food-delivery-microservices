package com.github.mehdihadeli.buildingblocks.jpa;

public final class HikariConfigConstants {

    // Timeout configurations
    public static final long CONNECTION_TIMEOUT = 30000L; // 30 seconds
    public static final long VALIDATION_TIMEOUT = 5000L; // 5 seconds
    public static final long IDLE_TIMEOUT = 300000L; // 5 minutes
    public static final long LEAK_DETECTION_THRESHOLD = 30000L; // 30 seconds
    public static final long MAX_LIFETIME = 1800000L; // 30 minutes

    // Pool configurations
    public static final int MAX_POOL_SIZE = 10; // Max connections in the pool
    public static final int MIN_IDLE = 5; // Minimum idle connections

    // Private constructor to prevent instantiation
    private HikariConfigConstants() {
        throw new UnsupportedOperationException("HikariConfigConstants class and cannot be instantiated");
    }
}
