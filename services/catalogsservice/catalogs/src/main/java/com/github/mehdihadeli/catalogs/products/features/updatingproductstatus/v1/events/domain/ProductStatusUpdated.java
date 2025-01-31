package com.github.mehdihadeli.catalogs.products.features.updatingproductstatus.v1.events.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record ProductStatusUpdated(ProductId productId, ProductStatus oldStatus, ProductStatus newStatus)
        implements IDomainEvent {
    public ProductStatusUpdated {
        notBeNull(productId, "productId");
        notBeNull(oldStatus, "oldStatus");
        notBeNull(newStatus, "newStatus");
    }
}