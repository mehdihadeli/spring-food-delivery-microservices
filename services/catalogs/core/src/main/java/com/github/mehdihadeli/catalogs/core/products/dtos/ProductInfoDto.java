package com.github.mehdihadeli.catalogs.core.products.dtos;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record ProductInfoDto(
        UUID id,
        UUID categoryId,
        String name,
        BigDecimal price,
        String size,
        ProductStatus status,
        @Nullable Set<ProductVariantDto> variants,
        @Nullable List<ProductReviewDto> reviews) {}
