package com.github.mehdihadeli.catalogs.categories.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record CategoryCode(String value) {
    public CategoryCode {
        notBeNullOrEmpty(value, "value");
    }
}