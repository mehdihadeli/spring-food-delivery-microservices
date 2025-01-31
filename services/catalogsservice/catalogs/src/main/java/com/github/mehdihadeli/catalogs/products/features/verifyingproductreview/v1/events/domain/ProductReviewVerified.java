package com.github.mehdihadeli.catalogs.products.features.verifyingproductreview.v1.events.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductReviewId;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record ProductReviewVerified(ProductId productId, ProductReviewId reviewId) implements IDomainEvent {

    public ProductReviewVerified {
        notBeNull(productId, "productId");
        notBeNull(reviewId, "reviewId");
    }
}