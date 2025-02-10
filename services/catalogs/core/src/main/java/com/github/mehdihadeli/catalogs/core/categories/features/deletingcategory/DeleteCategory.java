package com.github.mehdihadeli.catalogs.core.categories.features.deletingcategory;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommandUnit;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;


public record DeleteCategory(CategoryId categoryId) implements ITxCommandUnit
{}
