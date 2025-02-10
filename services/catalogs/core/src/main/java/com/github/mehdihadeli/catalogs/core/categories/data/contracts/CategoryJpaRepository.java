package com.github.mehdihadeli.catalogs.core.categories.data.contracts;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomJpaRepository;
import com.github.mehdihadeli.catalogs.core.categories.data.entities.CategoryDataModel;
import com.github.mehdihadeli.catalogs.core.categories.data.projections.CategoryInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface CategoryJpaRepository extends CustomJpaRepository<CategoryDataModel, UUID> {
  // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projection.dynamic
  // https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html#repositories.query-methods.query-creation
  // https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html
  // https://stackoverflow.com/questions/50879431/spring-jpa-projection-findall
  // - All we need is findBy() the part between `find` and `By` gets ignored by the query.
  // generation.
  // - Parsing query method names is divided into `subject` and `predicate` `find…By`.

  // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
  // in record we don't have nested projections
  Page<CategoryInfoProjection> findCategoryInfoProjectionBy(Pageable pageable);
}
