package com.github.mehdihadeli.buildingblocks.validation;

import java.util.function.Function;

public class NumberRuleBuilder<T> extends RuleBuilder<T, Number> {
    private final String fieldName;

    public NumberRuleBuilder(Validator<T> validator, Function<T, Number> property, String fieldName) {
        super(validator, property);
        this.fieldName = fieldName;
    }

    public NumberRuleBuilder<T> greaterThan(Number min) {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && ((Number) value).doubleValue() > min.doubleValue(),
                fieldName,
                "field must be greater than " + min + "."));
        return this;
    }

    public NumberRuleBuilder<T> lessThan(Number max) {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && ((Number) value).doubleValue() < max.doubleValue(),
                fieldName,
                "field must be less than " + max + "."));
        return this;
    }

    public NumberRuleBuilder<T> positive() {
        return greaterThan(0).withMessage("field must be positive.");
    }

    public NumberRuleBuilder<T> range(Number min, Number max) {
        return greaterThan(min).lessThan(max);
    }

    public NumberRuleBuilder<T> withMessage(String message) {
        ValidationRule<T> lastRule = validator.rules.get(validator.rules.size() - 1);
        validator.rules.set(
                validator.rules.size() - 1,
                new ValidationRule<>(lastRule.property(), lastRule.predicate(), fieldName, message));
        return this;
    }
}
