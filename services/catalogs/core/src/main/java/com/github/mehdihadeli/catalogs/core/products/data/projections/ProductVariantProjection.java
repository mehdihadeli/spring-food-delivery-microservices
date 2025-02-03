package com.github.mehdihadeli.catalogs.core.products.data.projections;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface ProductVariantProjection {
    UUID getId();

    String getSku();

    String getColor();

    Integer getStock();

    ProductProjection getProduct();

    PriceProjection getPrice();

    Map<String, String> getAttributes();

    interface PriceProjection {
        BigDecimal getAmount();
    }

    interface ProductProjection {
        UUID getId();
    }
}
