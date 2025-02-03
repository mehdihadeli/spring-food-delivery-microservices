package com.github.mehdihadeli.catalogs.core.products.exceptions;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductReviewId;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(ProductReviewId reviewId, ProductId productId) {
        super(String.format("Review '%s' for product '%s' not found.", reviewId.id(), productId.id()));
    }
}
