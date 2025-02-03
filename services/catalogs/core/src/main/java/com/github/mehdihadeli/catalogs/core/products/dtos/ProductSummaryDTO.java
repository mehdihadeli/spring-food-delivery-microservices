package com.github.mehdihadeli.catalogs.core.products.dtos;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record ProductSummaryDTO(
        UUID id,
        UUID categoryId,
        String name,
        PriceDTO price,
        SizeDTO size,
        ProductStatus status,
        @Nullable List<ProductVariantDTO> variants,
        @Nullable List<ProductReviewDTO> reviews) {
    public record PriceDTO(BigDecimal amount) {}

    public record SizeDTO(String size) {}

    public record ProductVariantDTO(UUID id, UUID productId, String sku, String color, Integer stock) {}

    public record ProductReviewDTO(UUID id, UUID productId, UUID customerId, Integer rating, String comment) {}
}
