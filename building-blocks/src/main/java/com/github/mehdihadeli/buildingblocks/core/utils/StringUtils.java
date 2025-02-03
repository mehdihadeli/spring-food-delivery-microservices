package com.github.mehdihadeli.buildingblocks.core.utils;

import com.google.common.base.CaseFormat;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Method to convert a camel case string to kebab case
    public static String toKebabCase(String input) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, input);
    }

    public static String toSnakeCase(String input) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
    }
}
