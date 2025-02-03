package com.github.mehdihadeli.catalogs.core.products.data.projections;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ReviewStatus;
import java.util.UUID;

public interface ProductReviewProjection {
    UUID getId();

    UUID getCustomerId();

    Integer getRating();

    String getComment();

    boolean isVerified();

    int getHelpfulVotes();

    ReviewStatus getStatus();

    ProductProjection getProduct();

    interface ProductProjection {
        UUID getId();
    }
}
