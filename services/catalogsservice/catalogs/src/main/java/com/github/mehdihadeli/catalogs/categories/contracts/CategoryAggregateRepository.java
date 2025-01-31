package com.github.mehdihadeli.catalogs.categories.contracts;

import com.github.mehdihadeli.catalogs.categories.models.entities.Category;
import com.github.mehdihadeli.catalogs.categories.models.valueobjects.CategoryId;

import java.util.Optional;

public interface CategoryAggregateRepository {
    Optional<Category> findById(CategoryId categoryId);

    Category add(Category category);

    void update(Category category);
}
