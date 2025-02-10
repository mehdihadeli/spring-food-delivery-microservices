package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

// - repositories will register automatically in they are in application package or if they are in another package by
// having `@EnableJpaRepositories` in our classpath the uses `JpaRepositoriesRegistrar` class
// - also for finding repository interfaces we can set `@AutoConfigurationPackage(basePackageClasses=packagename)`

// https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html
// https://docs.spring.io/spring-data/jpa/reference/repositories/query-by-example.html
// https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
// https://docs.spring.io/spring-data/jpa/reference/repositories/custom-implementations.html
// https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl
// https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html
// https://www.baeldung.com/hibernate-criteria-queries
// https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-criteria/persistence-criteria.html

// `NoRepositoryBean` This prevents Spring Data to try to create an instance of it directly and failing because it can’t
// determine the entity for that repository, since it still contains a generic type variable.  It is typically applied
// to base repository interfaces that are meant to be extended by other repository interfaces but should not be
// instantiated
@NoRepositoryBean
public interface CustomJpaRepository<TEntity, TID>
        extends JpaRepository<TEntity, TID>,
                ProjectionRepository,
                QuerydslPredicateExecutor<TEntity>,
                JpaSpecificationExecutor<TEntity> {
    @Override
    default void delete(TEntity entity) {
      // because in jpa like .net we can't change entity state entry to Modified for preventing delete operation
      // and change operation type to update so we have to override Delete JpaRepository and change it to update
      // if is instanceof SoftDeleteBase.
        if (entity instanceof SoftDeleteBase softDeleteBase) {
            softDeleteBase.setDeleted(true);
            save(entity); // Save the entity to update it instead of deleting it
        } else {
            throw new IllegalArgumentException("Entity must implement SoftDelete<TId>");
        }
    }

    @Override
    default void deleteById(TID id) {
        findById(id).ifPresent(this::delete);
    }
}

interface Test<S> {}
