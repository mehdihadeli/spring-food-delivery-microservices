package com.github.mehdihadeli.catalogs.core.categories.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record CategoryName(String value) {
    public CategoryName {
        notBeNullOrEmpty(value, "value");
    }
}
