package com.github.mehdihadeli.catalogs.core.categories.features.gettingcategorybyid;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.validation.Validator;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import org.springframework.stereotype.Component;

public record GetCategoryById(CategoryId categoryId) implements IQuery<GetCategoryByIdResult> {
    public GetCategoryById {
        notBeNull(categoryId, "categoryId");
    }
}

@Component
class GetCategoryByIdValidator extends Validator<GetCategoryById> {
    public GetCategoryByIdValidator() {
        objectRuleFor(GetCategoryById::categoryId, "categoryId").notNull();
    }
}
