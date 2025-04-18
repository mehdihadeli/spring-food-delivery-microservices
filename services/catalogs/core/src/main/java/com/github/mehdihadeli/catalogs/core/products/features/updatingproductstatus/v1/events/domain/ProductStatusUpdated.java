package com.github.mehdihadeli.catalogs.core.products.features.updatingproductstatus.v1.events.domain;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;

public record ProductStatusUpdated(ProductId productId, ProductStatus oldStatus, ProductStatus newStatus)
        implements IDomainEvent {
    public ProductStatusUpdated {
        notBeNull(productId, "productId");
        notBeNull(oldStatus, "oldStatus");
        notBeNull(newStatus, "newStatus");
    }
}
