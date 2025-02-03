package com.github.mehdihadeli.buildingblocks.core.utils;

import java.util.Arrays;
import org.springframework.core.env.Environment;

public final class EnvironmentUtils {
    private EnvironmentUtils() {}

    /**
     * Check if the current environment is "dev".
     *
     * @return True if "dev" is an active profile, false otherwise.
     */
    public static boolean isDev(Environment env) {
        if (env == null) {
            throw new IllegalStateException("EnvironmentUtils has not been initialized by Spring.");
        }
        return Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equalsIgnoreCase("dev"));
    }

    /**
     * Check if the current environment is "production".
     *
     * @return True if "production" is an active profile, false otherwise.
     */
    public static boolean isProduction(Environment env) {
        if (env == null) {
            throw new IllegalStateException("EnvironmentUtils has not been initialized by Spring.");
        }
        return Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equalsIgnoreCase("prod"));
    }
}
