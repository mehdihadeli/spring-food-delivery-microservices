package com.github.mehdihadeli.buildingblocks.core.data.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNegativeOrZero;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public Money {
        notBeNegativeOrZero(amount, "amount");
        notBeNullOrEmpty(currency, "currency");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
