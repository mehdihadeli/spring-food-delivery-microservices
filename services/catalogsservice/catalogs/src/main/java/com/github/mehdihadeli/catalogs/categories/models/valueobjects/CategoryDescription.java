package com.github.mehdihadeli.catalogs.categories.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record CategoryDescription(String value) {
    public CategoryDescription {
        notBeNullOrEmpty(value, "value");
    }
}
