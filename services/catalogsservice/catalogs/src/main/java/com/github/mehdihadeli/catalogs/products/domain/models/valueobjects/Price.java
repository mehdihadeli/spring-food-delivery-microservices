package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import java.math.BigDecimal;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNegativeOrZero;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record Price(BigDecimal amount, String currency) {
    public Price {
        notBeNegativeOrZero(amount, "amount");
        notBeNullOrEmpty(currency, "currency");
    }

    /**
     * Adjusts the price by a given percentage. Positive percentage increases the amount,
     * while negative percentage decreases it.
     *
     * @param percentage The percentage to adjust the price by (e.g., 10 for +10%, -5 for -5%).
     * @return A new Price instance with the adjusted amount.
     */
    public Price adjustByPercentage(double percentage) {
        BigDecimal factor = BigDecimal.valueOf(1 + (percentage / 100));
        BigDecimal adjustedAmount = this.amount.multiply(factor);
        return new Price(adjustedAmount, this.currency);
    }
}
