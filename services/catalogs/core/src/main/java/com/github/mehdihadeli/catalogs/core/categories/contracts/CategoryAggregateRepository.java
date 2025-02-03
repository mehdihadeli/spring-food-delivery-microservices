package com.github.mehdihadeli.catalogs.core.categories.contracts;

import com.github.mehdihadeli.catalogs.core.categories.models.entities.Category;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import java.util.Optional;

public interface CategoryAggregateRepository {
    Optional<Category> findById(CategoryId categoryId);

    Category add(Category category);

    void update(Category category);
}
