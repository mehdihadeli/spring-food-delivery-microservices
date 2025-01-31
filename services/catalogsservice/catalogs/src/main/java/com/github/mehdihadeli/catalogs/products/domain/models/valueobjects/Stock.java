package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNegativeOrZero;

public record Stock(int quantity) {
    public Stock {
        notBeNegativeOrZero(quantity, "quantity");
    }

    public Stock update(int change) {
        int newQuantity = this.quantity + change;
        notBeNegativeOrZero(newQuantity, "newQuantity");

        return new Stock(newQuantity);
    }

    public boolean isAvailable() {
        return quantity > 0;
    }
}