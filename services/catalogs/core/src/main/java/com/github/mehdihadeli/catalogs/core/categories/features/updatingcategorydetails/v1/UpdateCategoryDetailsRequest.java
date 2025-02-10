package com.github.mehdihadeli.catalogs.core.categories.features.updatingcategorydetails.v1;

import org.springframework.lang.Nullable;

public record UpdateCategoryDetailsRequest(String name, String code, @Nullable String description) {}
