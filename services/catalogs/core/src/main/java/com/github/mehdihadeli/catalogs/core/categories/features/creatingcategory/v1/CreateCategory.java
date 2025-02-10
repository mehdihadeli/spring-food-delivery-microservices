package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommand;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import org.springframework.lang.Nullable;

public record CreateCategory(CategoryId categoryId, Name name, Code code, @Nullable Description description)
        implements ITxCommand<CreateCategoryResult> {
    public CreateCategory {
        ValidationUtils.notBeNull(categoryId, "categoryId");
        ValidationUtils.notBeNull(name, "name");
        ValidationUtils.notBeNull(code, "code");
    }
}
