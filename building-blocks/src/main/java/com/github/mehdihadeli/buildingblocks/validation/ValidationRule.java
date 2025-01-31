package com.github.mehdihadeli.buildingblocks.validation;

import java.util.function.Function;
import java.util.function.Predicate;

public record ValidationRule<T>(
        Function<T, ?> property, Predicate<Object> predicate, String fieldName, String errorMessage) {
    public boolean isValid(T object) {
        Object value = property.apply(object);
        return predicate.test(value);
    }
}
