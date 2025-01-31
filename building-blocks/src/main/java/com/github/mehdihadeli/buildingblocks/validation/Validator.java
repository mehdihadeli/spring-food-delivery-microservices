package com.github.mehdihadeli.buildingblocks.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class Validator<T> {
    protected final List<ValidationRule<T>> rules = new ArrayList<>();

    public StringRuleBuilder<T> stringRuleFor(Function<T, String> property, String fieldName) {
        return new StringRuleBuilder<>(this, property, fieldName);
    }

    public NumberRuleBuilder<T> numberRuleFor(Function<T, Number> property, String fieldName) {
        return new NumberRuleBuilder<>(this, property, fieldName);
    }

    public UUIDRuleBuilder<T> uuidRuleFor(Function<T, UUID> property, String fieldName) {
        return new UUIDRuleBuilder<>(this, property, fieldName);
    }

    public <P> ObjectRuleBuilder<T, P> objectRuleFor(Function<T, P> property, String fieldName) {
        return new ObjectRuleBuilder<>(this, property, fieldName);
    }

    void addRule(ValidationRule<T> rule) {
        rules.add(rule);
    }

    public ValidationResult validate(T object) {
        ValidationResult result = new ValidationResult();
        for (ValidationRule<T> rule : rules) {
            if (!rule.isValid(object)) {
                result.addError(rule.fieldName(), rule.errorMessage());
            }
        }
        return result;
    }
}
