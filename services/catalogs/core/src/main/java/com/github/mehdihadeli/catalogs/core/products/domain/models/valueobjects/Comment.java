package com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;

public record Comment(String text) {
    public Comment {
        ValidationUtils.notBeNullOrEmpty(text, "text");
        if (text.length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters.");
        }
    }
}
