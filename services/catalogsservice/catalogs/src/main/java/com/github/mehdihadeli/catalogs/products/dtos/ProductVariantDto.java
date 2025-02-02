package com.github.mehdihadeli.catalogs.products.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantDto(
        UUID variantId,
        String sku,
        BigDecimal amount,
        String currency,
        int stock,
        String color) {}
