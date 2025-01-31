package com.github.mehdihadeli.buildingblocks.validation;

import java.util.function.Function;

public class StringRuleBuilder<T> extends RuleBuilder<T, String> {
    private final String fieldName;

    public StringRuleBuilder(Validator<T> validator, Function<T, String> property, String fieldName) {
        super(validator, property);
        this.fieldName = fieldName;
    }

    public StringRuleBuilder<T> notEmpty() {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && !((String) value).isEmpty(),
                fieldName,
                "field must not be empty."));
        return this;
    }

    public StringRuleBuilder<T> minLength(int length) {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && ((String) value).length() >= length,
                fieldName,
                "field must be at least " + length + " characters long."));
        return this;
    }

    public StringRuleBuilder<T> maxLength(int length) {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && ((String) value).length() <= length,
                fieldName,
                "field must be at most " + length + " characters long."));
        return this;
    }

    public StringRuleBuilder<T> matches(String regex) {
        validator.addRule(new ValidationRule<>(
                property,
                value -> value != null && ((String) value).matches(regex),
                fieldName,
                "field must match the pattern: " + regex));
        return this;
    }

    public StringRuleBuilder<T> email() {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return matches(emailRegex).withMessage("field must be a valid email.");
    }

    public StringRuleBuilder<T> withMessage(String message) {
        ValidationRule<T> lastRule = validator.rules.get(validator.rules.size() - 1);
        validator.rules.set(
                validator.rules.size() - 1,
                new ValidationRule<>(lastRule.property(), lastRule.predicate(), fieldName, message));
        return this;
    }
}
