package com.github.mehdihadeli.catalogs.core.products.dtos;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ReviewStatus;
import java.util.UUID;

public record ProductReviewDto(
        UUID reviewId,
        UUID customerId,
        int rating,
        String comment,
        boolean verified,
        int helpfulVotes,
        ReviewStatus status) {}
