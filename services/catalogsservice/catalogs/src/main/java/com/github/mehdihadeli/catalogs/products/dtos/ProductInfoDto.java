package com.github.mehdihadeli.catalogs.products.dtos;

import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProductInfoDto(
        UUID id,
        UUID categoryId,
        String name,
        BigDecimal price,
        String size,
        ProductStatus status,
        @Nullable Set<ProductVariantDto> variants,
        @Nullable List<ProductReviewDto> reviews) {}
