package com.github.mehdihadeli.catalogs.products.data.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;


@Embeddable
public class MoneyVO {

    private BigDecimal amount;
    private String currency;

    // Default constructor for JPA
    public MoneyVO() {}

    public MoneyVO(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
