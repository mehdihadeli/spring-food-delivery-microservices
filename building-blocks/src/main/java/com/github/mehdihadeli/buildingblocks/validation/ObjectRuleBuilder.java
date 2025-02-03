package com.github.mehdihadeli.buildingblocks.validation;

import java.util.function.Function;

public class ObjectRuleBuilder<T, P> extends RuleBuilder<T, P> {

    private final String fieldName;

    public ObjectRuleBuilder(Validator<T> validator, Function<T, P> property, String fieldName) {
        super(validator, property);
        this.fieldName = fieldName;
    }

    public ObjectRuleBuilder<T, P> notNull() {
        validator.addRule(new ValidationRule<>(
                property, x -> x != null, fieldName, String.format("filed %s should not be null.", fieldName)));
        return this;
    }

    public ObjectRuleBuilder<T, P> withMessage(String message) {
        ValidationRule<T> lastRule = validator.rules.get(validator.rules.size() - 1);
        validator.rules.set(
                validator.rules.size() - 1,
                new ValidationRule<>(lastRule.property(), lastRule.predicate(), fieldName, message));
        return this;
    }
}
