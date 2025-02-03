package com.github.mehdihadeli.catalogs.products.exceptions;

import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.CustomerId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;

public class DuplicateReviewException extends DomainException {
    public DuplicateReviewException(CustomerId customerId, ProductId productId) {
        super(String.format("Customer %s has already reviewed product %s", customerId, productId));
    }
}
