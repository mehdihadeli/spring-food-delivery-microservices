package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;

public record Color(String value) {
    public Color {
        ValidationUtils.notBeNullOrEmpty(value, "value");
    }
}
