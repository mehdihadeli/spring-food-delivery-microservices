package com.github.mehdihadeli.catalogs.products.features.creatingproductvariant.v1.events.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Money;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.*;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

// - it is better we pass necessary properties instead of passing full entity and tightly couple to it.
// - The event consumers now need to understand the internal details of full ProductVariant.
// - The entire ProductVariant entity, potentially with large internal structures and references, is passed around,
// which could be inefficient.
public record ProductVariantCreated(
        ProductId productId,
        VariantId variantId,
        SKU sku,
        Money price,
        Stock stock,
        Color color)
        implements IDomainEvent {

    public ProductVariantCreated {
        // Validation to ensure no nulls
        notBeNull(productId, "productId");
        notBeNull(variantId, "variantId");
        notBeNull(sku, "sku");
        notBeNull(price, "price");
        notBeNull(stock, "stock");
        notBeNull(color, "color");
    }
}