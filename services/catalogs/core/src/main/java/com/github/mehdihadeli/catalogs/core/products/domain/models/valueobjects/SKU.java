package com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record SKU(String value) {
    public SKU {
        notBeNullOrEmpty(value, "value");
    }
}
