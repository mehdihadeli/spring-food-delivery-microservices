package com.github.mehdihadeli.buildingblocks.validation;

import java.util.UUID;
import java.util.function.Function;

public class UUIDRuleBuilder<T> extends RuleBuilder<T, UUID> {

    private final String fieldName;

    public UUIDRuleBuilder(Validator<T> validator, Function<T, UUID> property, String fieldName) {
        super(validator, property);
        this.fieldName = fieldName;
    }

    public UUIDRuleBuilder<T> notEmpty() {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && !value.toString().equals("00000000-0000-0000-0000-000000000000"),
                fieldName,
                "UUID must not be empty."));
        return this;
    }

    public UUIDRuleBuilder<T> empty() {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value == null || value.toString().equals("00000000-0000-0000-0000-000000000000"),
                fieldName,
                "UUID must be empty."));
        return this;
    }

    public UUIDRuleBuilder<T> withMessage(String message) {
        ValidationRule<T> lastRule = validator.rules.get(validator.rules.size() - 1);
        validator.rules.set(
                validator.rules.size() - 1,
                new ValidationRule<>(lastRule.property(), lastRule.predicate(), fieldName, message));
        return this;
    }
}
