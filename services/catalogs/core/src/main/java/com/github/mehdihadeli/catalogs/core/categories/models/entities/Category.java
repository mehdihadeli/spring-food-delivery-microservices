package com.github.mehdihadeli.catalogs.core.categories.models.entities;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1.events.domain.CategoryCreated;
import com.github.mehdihadeli.catalogs.core.categories.features.updatingcategorydetails.v1.events.domain.CategoryDetailsUpdated;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import java.util.Optional;
import org.springframework.lang.Nullable;

public class Category extends Aggregate<CategoryId> {
    private Name name;
    private Code code;

    @Nullable
    private Description description;

    private Category(CategoryId categoryId, Name name, Code code, @Nullable Description description) {

        super.setId(categoryId);
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public static Category create(CategoryId categoryId, Name name, Code code, @Nullable Description description) {
        // factory method serves as the clear entry point for domain validation
        var category = new Category(
                notBeNull(categoryId, "categoryId"), notBeNull(name, "name"), notBeNull(code, "code"), description);

        category.addDomainEvent(new CategoryCreated(categoryId, name, code, description));

        return category;
    }

    public Name getName() {
        return name;
    }

    public Code getCode() {
        return code;
    }

    public Optional<Description> getDescription() {
        return Optional.ofNullable(description);
    }

    public void updateCategoryDetails(Name newName, Code newCode, Description newDescription) {
        notBeNull(newName, "newName");
        notBeNull(newCode, "newCode");
        notBeNull(newDescription, "newDescription");

        this.name = newName;
        this.code = newCode;
        this.description = newDescription;

        this.addDomainEvent(new CategoryDetailsUpdated(getId(), name, code, description));
    }
}
