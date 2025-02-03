package com.github.mehdihadeli.catalogs.core.products.features.creatingproductreview.v1.events.domain;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.*;

// - it is better we pass necessary properties instead of passing full entity and tightly couple to it.
// - The event consumers now need to understand the internal details of full ProductVariant.
// - The entire ProductVariant entity, potentially with large internal structures and references, is passed around,
// which could be inefficient.

public record ProductReviewCreated(
        ProductId productId, ProductReviewId reviewId, CustomerId customerId, Rating rating, Comment comment)
        implements IDomainEvent {

    public ProductReviewCreated {
        // Validate required properties are not null
        notBeNull(productId, "productId");
        notBeNull(reviewId, "reviewId");
        notBeNull(customerId, "customerId");
        notBeNull(rating, "rating");
        notBeNull(comment, "comment");
    }
}
