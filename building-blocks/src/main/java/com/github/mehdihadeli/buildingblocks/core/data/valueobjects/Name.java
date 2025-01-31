package com.github.mehdihadeli.buildingblocks.core.data.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record Name(String value) {
    public Name {
        notBeNullOrEmpty(value, "name");
    }
}
