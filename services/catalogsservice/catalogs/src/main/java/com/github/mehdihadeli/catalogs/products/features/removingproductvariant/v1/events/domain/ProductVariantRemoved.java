package com.github.mehdihadeli.catalogs.products.features.removingproductvariant.v1.events.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.VariantId;

// - it is better we pass necessary properties instead of passing full entity and tightly couple to it.
// - The event consumers now need to understand the internal details of full ProductVariant.
// - The entire ProductVariant entity, potentially with large internal structures and references, is passed around,
// which could be inefficient.
public record ProductVariantRemoved(ProductId productId, VariantId variantId) implements IDomainEvent {
    public ProductVariantRemoved {
        ValidationUtils.notBeNull(productId, "productId");
        ValidationUtils.notBeNull(variantId, "variantId");
    }
}