package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1;

import org.springframework.lang.Nullable;

public record CreateCategoryRequest(String name, String code, @Nullable String description) {}
