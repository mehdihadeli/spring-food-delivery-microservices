package com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNegativeOrZero;

public record Dimensions(double width, double height, double depth) {
    public Dimensions {
        notBeNegativeOrZero(width, "width");
        notBeNegativeOrZero(height, "height");
        notBeNegativeOrZero(depth, "depth");
    }
}
