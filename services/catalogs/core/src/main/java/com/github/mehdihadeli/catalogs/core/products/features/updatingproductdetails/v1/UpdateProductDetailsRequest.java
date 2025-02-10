package com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1;

import java.math.BigDecimal;
import org.springframework.lang.Nullable;

public record UpdateProductDetailsRequest(
        String name,
        BigDecimal priceAmount,
        String currency,
        double width,
        double height,
        double depth,
        String size,
        String sizeUnit,
        @Nullable String description) {}
