package com.github.mehdihadeli.catalogs.core.products.exceptions;

import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;

public class InvalidStatusTransitionException extends DomainException {
    public InvalidStatusTransitionException(ProductStatus currentStatus, ProductStatus newStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
    }
}
