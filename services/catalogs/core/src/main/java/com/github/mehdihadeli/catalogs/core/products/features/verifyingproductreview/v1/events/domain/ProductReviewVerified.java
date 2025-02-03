package com.github.mehdihadeli.catalogs.core.products.features.verifyingproductreview.v1.events.domain;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductReviewId;

public record ProductReviewVerified(ProductId productId, ProductReviewId reviewId) implements IDomainEvent {

    public ProductReviewVerified {
        notBeNull(productId, "productId");
        notBeNull(reviewId, "reviewId");
    }
}
