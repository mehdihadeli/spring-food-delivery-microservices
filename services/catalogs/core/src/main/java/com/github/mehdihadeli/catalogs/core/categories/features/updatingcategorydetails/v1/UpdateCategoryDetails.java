package com.github.mehdihadeli.catalogs.core.categories.features.updatingcategorydetails.v1;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommandUnit;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import org.springframework.lang.Nullable;

public record UpdateCategoryDetails(
        CategoryId categoryId, Name newName, Code newCode, @Nullable Description newDescription)
        implements ITxCommandUnit {
    public UpdateCategoryDetails {
        notBeNull(categoryId, "categoryId");
        notBeNull(newName, "newName");
        notBeNull(newCode, "newCode");
    }
}
