package com.github.mehdihadeli.buildingblocks.core.data.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record Description(String value) {
    public Description {
        notBeNullOrEmpty(value, "description");
    }
}
