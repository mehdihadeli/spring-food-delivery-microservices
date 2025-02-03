package com.github.mehdihadeli.catalogs.core.products.data.projections;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductSummaryProjection {
    UUID getId();

    UUID getCategoryId();

    String getName();

    PriceProjection getPrice();

    SizeProjection getSize();

    ProductStatus getStatus();

    interface PriceProjection {
        BigDecimal getAmount();
    }

    interface SizeProjection {
        String getSize();
    }

    Set<ProductVariantProjection> getVariants();

    List<ProductReviewProjection> getReviews();
}
