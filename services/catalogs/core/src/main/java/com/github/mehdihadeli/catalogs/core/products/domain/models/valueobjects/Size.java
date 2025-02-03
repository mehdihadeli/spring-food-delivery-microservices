package com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;

public record Size(String size, String unit) {
    public Size {
        ValidationUtils.notBeNullOrEmpty(size, "size");
        ValidationUtils.notBeNullOrEmpty(unit, "unit");
    }
}
