package com.github.mehdihadeli.catalogs.products.domain.models.entities;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Money;
import com.github.mehdihadeli.buildingblocks.core.domain.EntityBase;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Color;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.SKU;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Stock;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.VariantId;

import java.math.BigDecimal;
import java.util.Objects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

// - It has a distinct identity (variantId) that remains constant throughout its lifecycle
// - Even if two variants have identical attributes (same price, SKU, stock level), they are still different objects
// because of their unique identity
// - for example a blue t-shirt in size M and another blue t-shirt in size M are separate variants even if they share
// the same price and other attributes
// - Unlike value objects which are immutable, entities can change over time
public class ProductVariant extends EntityBase<VariantId> {
    private final SKU sku;

    private Money price;

    private Stock stock;
    private Color color;

    public ProductVariant(
            VariantId variantId,
            SKU sku,
            Money price,
            Stock stock,
            Color color) {
        this.setId(notBeNull(variantId, "variantId"));
        this.sku = notBeNull(sku, "sku");
        this.price = notBeNull(price, "price");
        this.stock = notBeNull(stock, "stock");
        this.color = notBeNull(color, "color");
    }

    // Private default constructor for Jackson serializer
    private ProductVariant() {
        this.sku = null;
    }

    public void updateStock(int quantity) {
        this.stock = stock.update(quantity);
    }

    public void updateColor(Color color) {
        this.color = Objects.requireNonNull(color, "Color cannot be null");
    }

    public boolean isInStock() {
        return stock.isAvailable();
    }

    public void applyDiscount(double percentageOff) {
        if (percentageOff < 0 || percentageOff > 100) {
            throw new IllegalArgumentException("Invalid discount percentage");
        }
        BigDecimal discountFactor =
                BigDecimal.ONE.subtract(BigDecimal.valueOf(percentageOff).divide(BigDecimal.valueOf(100)));
        this.price = new Money(this.price.amount().multiply(discountFactor), this.price.currency());
    }

    public SKU getSku() {
        return sku;
    }

    public Money getPrice() {
        return price;
    }

    public Stock getStock() {
        return stock;
    }

    public Color getColor() {
        return color;
    }

    // Identity-based equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant)) return false;
        ProductVariant that = (ProductVariant) o;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}