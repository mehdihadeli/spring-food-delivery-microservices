package com.github.mehdihadeli.catalogs.core.categories;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.categories.data.entities.CategoryDataModel;
import com.github.mehdihadeli.catalogs.core.categories.data.projections.CategoryInfoProjection;
import com.github.mehdihadeli.catalogs.core.categories.dtos.CategoryDto;
import com.github.mehdihadeli.catalogs.core.categories.dtos.CategoryInfoDto;
import com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1.CreateCategory;
import com.github.mehdihadeli.catalogs.core.categories.models.entities.Category;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static Category toCategoryAggregate(CategoryDataModel categoryDataModel) {
        var categoryId = new CategoryId(categoryDataModel.getId());
        var name = new Name(categoryDataModel.getName());
        var code = new Code(categoryDataModel.getCode());
        var description =
                categoryDataModel.getDescription() != null ? new Description(categoryDataModel.getDescription()) : null;

        return Category.create(categoryId, name, code, description);
    }

    public static CategoryDataModel toCategoryDataModel(Category category) {
        return toCategoryDataModel(category, null);
    }

    public static CategoryDataModel toCategoryDataModel(Category category, CategoryDataModel existingDataModel) {
        CategoryDataModel categoryDataModel = existingDataModel == null ? new CategoryDataModel() : existingDataModel;

        categoryDataModel.setId(category.getId().id());
        categoryDataModel.setName(category.getName().value());
        categoryDataModel.setCode(category.getCode().value());
        categoryDataModel.setDescription(
                category.getDescription().map(Description::value).orElse(null));

        categoryDataModel.addDomainEvents(category.dequeueUncommittedDomainEvents());

        return categoryDataModel;
    }

    public static Category toCategoryAggregate(CreateCategory createCategory) {
        return Category.create(
                createCategory.categoryId(),
                createCategory.name(),
                createCategory.code(),
                createCategory.description());
    }

    public static CategoryInfoDto toCategoryInfoDto(CategoryInfoProjection projection) {
        if (projection == null) {
            return null;
        }

        return new CategoryInfoDto(projection.id(), projection.name(), projection.code(), projection.description());
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDto(
                category.getId().id(),
                category.getName().value(),
                category.getCode().value(),
                category.getDescription().map(Description::value).orElse(null));
    }

    // read models
}
