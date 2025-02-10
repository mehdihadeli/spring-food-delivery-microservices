package com.github.mehdihadeli.catalogs.core.categories.data.contracts;

import com.github.mehdihadeli.catalogs.core.categories.data.projections.CategoryInfoProjection;
import com.github.mehdihadeli.catalogs.core.categories.models.entities.Category;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryAggregateRepository {
    Optional<Category> findById(CategoryId categoryId);

    Category add(Category category);

    void delete(Category product);

    void update(Category category);

    Page<CategoryInfoProjection> findByPage(Pageable pageable);
}
