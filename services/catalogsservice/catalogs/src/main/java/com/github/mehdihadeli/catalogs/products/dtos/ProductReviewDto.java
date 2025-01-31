package com.github.mehdihadeli.catalogs.products.dtos;

import com.github.mehdihadeli.catalogs.products.domain.models.entities.ReviewStatus;

import java.util.UUID;

public record ProductReviewDto(
        UUID reviewId,
        UUID customerId,
        int rating,
        String comment,
        boolean verified,
        int helpfulVotes,
        ReviewStatus status) {}
