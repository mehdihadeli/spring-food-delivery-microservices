// package com.mehdihadeli.verticalslicetemplate.categories.data;
//
// import com.mehdihadeli.buildingblocks.core.data.AggregateBaseRepository;
// import com.mehdihadeli.verticalslicetemplate.categories.contracts.CategoryAggregateRepository;
// import com.mehdihadeli.verticalslicetemplate.categories.contracts.CategoryJpaRepository;
// import com.mehdihadeli.verticalslicetemplate.categories.data.entities.CategoryDataModel;
// import com.mehdihadeli.verticalslicetemplate.categories.models.entities.Category;
// import com.mehdihadeli.verticalslicetemplate.categories.models.valueobjects.CategoryId;
// import jakarta.persistence.EntityManager;
// import org.springframework.stereotype.Repository;
//
// import java.util.Optional;
// import java.util.UUID;
//
// @Repository
// public class CategoryAggregateRepositoryImpl
//        extends AggregateBaseRepository<Category, CategoryDataModel, CategoryId, UUID>
//        implements CategoryAggregateRepository {
//
//    private final CategoryJpaRepository categoryJpaRepository;
//
//    protected CategoryAggregateRepositoryImpl(
//            EntityManager entityManager, CategoryJpaRepository categoryJpaRepository) {
//        super(entityManager, categoryJpaRepository);
//        this.categoryJpaRepository = categoryJpaRepository;
//    }
//
//    @Override
//    public Category toAggregate(CategoryDataModel categoryDataModel) {
//        return null;
//    }
//
//    @Override
//    public CategoryDataModel toDataModel(Category category) {
//        return null;
//    }
//
//    @Override
//    public CategoryDataModel toDataModel(Category category, CategoryDataModel categoryDataModel) {
//        return null;
//    }
//
//    @Override
//    public Optional<Category> findById(CategoryId categoryId) {
//        return Optional.empty();
//    }
// }
