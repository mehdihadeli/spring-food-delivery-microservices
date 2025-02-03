package com.github.mehdihadeli.buildingblocks.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts the current LocalDateTime to epoch seconds based on the system's default time zone.
     *
     * @return epoch seconds as a long
     */
    public static long getCurrentEpochSecond() {
        return LocalDateTime.now()
                .toEpochSecond(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()));
    }

    /**
     * Converts a given LocalDateTime to epoch seconds based on the system's default time zone.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return epoch seconds as a long
     */
    public static long toEpochSecond(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(localDateTime));
    }
}
