package com.github.mehdihadeli.catalogs.products.dtos;

import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record ProductVariantDto(
        UUID variantId,
        String sku,
        BigDecimal amount,
        String currency,
        int stock,
        String color,
        @Nullable Map<String, String> attributes) {}
