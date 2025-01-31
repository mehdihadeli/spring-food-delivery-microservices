package com.github.mehdihadeli.buildingblocks.validation;

import java.util.function.Function;

public abstract class RuleBuilder<T, P> {
    protected final Validator<T> validator;
    protected final Function<T, P> property;
    protected final String fieldName;

    public RuleBuilder(Validator<T> validator, Function<T, P> property) {
        this.validator = validator;
        this.property = property;
        this.fieldName = extractFieldName(property);
    }

    public RuleBuilder<T, P> notNull() {
        validator.addRule(new ValidationRule<>(property, value -> value != null, fieldName, "field must not be null."));
        return this;
    }

    public RuleBuilder<T, P> nullValue() {
        validator.addRule(new ValidationRule<>(property, value -> value == null, fieldName, "field must be null."));
        return this;
    }

    private String extractFieldName(Function<T, ?> property) {
        String methodName = property.toString();
        return methodName.substring(methodName.lastIndexOf("::") + 2);
    }
}
