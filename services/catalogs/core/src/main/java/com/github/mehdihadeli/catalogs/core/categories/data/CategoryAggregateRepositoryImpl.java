package com.github.mehdihadeli.catalogs.core.categories.data;

import com.github.mehdihadeli.buildingblocks.core.data.AggregateBaseRepository;
import com.github.mehdihadeli.catalogs.core.categories.CategoryMapper;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryJpaRepository;
import com.github.mehdihadeli.catalogs.core.categories.data.entities.CategoryDataModel;
import com.github.mehdihadeli.catalogs.core.categories.data.projections.CategoryInfoProjection;
import com.github.mehdihadeli.catalogs.core.categories.models.entities.Category;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

// The `@Repository` annotation needs to be on the concrete class not interface and after that interface is accessible
// on dependency injection
@Repository
public class CategoryAggregateRepositoryImpl
        extends AggregateBaseRepository<Category, CategoryDataModel, CategoryId, UUID>
        implements CategoryAggregateRepository {
    private final EntityManager entityManager;
    CategoryJpaRepository categoryJpaRepository;

    protected CategoryAggregateRepositoryImpl(
            EntityManager entityManager, CategoryJpaRepository categoryJpaRepository) {
        super(entityManager, categoryJpaRepository);
        this.entityManager = entityManager;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Category toAggregate(CategoryDataModel categoryDataModel) {
        return CategoryMapper.toCategoryAggregate(categoryDataModel);
    }

    @Override
    public CategoryDataModel toDataModel(Category category) {
        return CategoryMapper.toCategoryDataModel(category);
    }

    @Override
    public CategoryDataModel toDataModel(Category category, CategoryDataModel categoryDataModel) {
        return CategoryMapper.toCategoryDataModel(category, categoryDataModel);
    }

    @Override
    public Page<CategoryInfoProjection> findByPage(Pageable pageable) {
        return categoryJpaRepository.findCategoryInfoProjectionBy(pageable);
    }
}
