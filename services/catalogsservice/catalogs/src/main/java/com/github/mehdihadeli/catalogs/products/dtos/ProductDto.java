package com.github.mehdihadeli.catalogs.products.dtos;

import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record ProductDto(
        UUID Id,
        UUID categoryId,
        String name,
        ProductStatus status,
        BigDecimal priceAmount,
        String currency,
        double width,
        double height,
        double depth,
        @Nullable Set<ProductVariantDto> variants,
        @Nullable List<ProductReviewDto> reviews,
        @Nullable String description) {}
