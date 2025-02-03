package com.github.mehdihadeli.catalogs.core.categories.models.entities;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryCode;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryDescription;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryName;
import java.util.Optional;
import org.springframework.lang.Nullable;

public class Category extends Aggregate<CategoryId> {
    private CategoryName name;
    private CategoryCode code;

    @Nullable
    private CategoryDescription description;

    private Category(
            CategoryId categoryId, CategoryName name, CategoryCode code, @Nullable CategoryDescription description) {

        super.setId(categoryId);
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public static Category create(
            CategoryId categoryId, CategoryName name, CategoryCode code, @Nullable CategoryDescription description) {

        // factory method serves as the clear entry point for domain validation
        return new Category(
                notBeNull(categoryId, "categoryId"), notBeNull(name, "name"), notBeNull(code, "code"), description);
    }

    public CategoryName getName() {
        return name;
    }

    public CategoryCode getCode() {
        return code;
    }

    public Optional<CategoryDescription> getDescription() {
        return Optional.ofNullable(description);
    }

    public void rename(CategoryName newName) {
        notBeNull(newName, "newName");
        this.name = newName;

        // domain event
    }

    public void updateCode(CategoryCode newCode) {
        notBeNull(newCode, "newCode");
        this.code = newCode;

        // domain event
    }

    public void updateDescription(@Nullable CategoryDescription newDescription) {
        this.description = newDescription;

        // domain event
    }
}
