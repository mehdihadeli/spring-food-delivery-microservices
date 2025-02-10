package com.github.mehdihadeli.buildingblocks.core.data.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

public record Code(String value) {
    public Code {
        notBeNullOrEmpty(value, "value");
    }
}
