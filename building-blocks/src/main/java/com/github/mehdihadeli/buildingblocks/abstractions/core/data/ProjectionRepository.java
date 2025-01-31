package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ProjectionRepository {
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projection.dynamic
    // https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html#repositories.query-methods.query-creation
    // https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html
    // https://stackoverflow.com/questions/50879431/spring-jpa-projection-findall
    // - All we need is findBy() the part between `find` and `By` gets ignored by the query.
    // generation.
    // - Parsing query method names is divided into `subject` and `predicate` `find…By`.
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projection.dynamic
    <T> Page<T> findBy(Class<T> projection, Pageable pageable);

    <T> List<T> findBy(Class<T> projection);
}
