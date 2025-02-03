package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record CreateProductRequest(
        UUID categoryId,
        String name,
        ProductStatus status,
        BigDecimal priceAmount,
        String currency,
        double width,
        double height,
        double depth,
        String size,
        String sizeUnit,
        @Nullable Set<ProductVariantRequest> productVariants,
        @Nullable String description) {
    public record ProductVariantRequest(
            String sku,
            BigDecimal amount,
            String currency,
            int stock,
            String color,
            @Nullable Map<String, String> attributes) {}
}
