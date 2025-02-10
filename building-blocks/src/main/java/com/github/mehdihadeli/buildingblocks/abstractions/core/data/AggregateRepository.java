package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import com.github.mehdihadeli.buildingblocks.core.data.EntityDataModelBase;
import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AggregateRepository<
                TAggregate extends Aggregate<TAggregateId>,
                TDataModelEntity extends EntityDataModelBase<TDataModelEntityId>,
                TAggregateId extends IdentityId<TDataModelEntityId>,
                TDataModelEntityId extends Serializable>
        extends EntityMapper<TAggregate, TDataModelEntity> {
    EntityManager getEntityManager();

    CustomJpaRepository<TDataModelEntity, TDataModelEntityId> getCustomJpaRepository();

    // Regular CRUD operations
    TAggregate add(TAggregate aggregate);

    void update(TAggregate aggregate);

    TAggregate save(TAggregate aggregate);

    Optional<TAggregate> findById(TAggregateId id);

    void deleteById(TAggregateId id);

    void delete(TAggregate aggregate);

    // Methods using JPA Specification
    List<TAggregate> findAll(Specification<TDataModelEntity> spec);

    Optional<TAggregate> findOne(Specification<TDataModelEntity> spec);

    Page<TAggregate> findAll(Specification<TDataModelEntity> spec, Pageable pageable);

    // Methods using QBE
    List<TAggregate> findAllByExample(TDataModelEntity exampleEntity);

    Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity);

    Page<TAggregate> findAllByExample(TDataModelEntity exampleEntity, Pageable pageable);

    List<TAggregate> findAllByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher);

    Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher);

    Page<TAggregate> findAllByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher, Pageable pageable);

    // CriteriaQuery support
    List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder);

    Page<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Pageable pageable);

    List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Sort sort);

    Optional<TAggregate> findOneByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder);

    long countByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder);

    boolean existsByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder);

    // Helper method to create Sort.Order into JPA Order
    default List<jakarta.persistence.criteria.Order> createOrders(CriteriaBuilder cb, Root<?> root, Sort sort) {
        List<Order> orders = new ArrayList<>();

        for (Sort.Order sortOrder : sort) {
            Path<?> path = getPropertyPath(root, sortOrder.getProperty());
            orders.add(sortOrder.isAscending() ? cb.asc(path) : cb.desc(path));
        }

        return orders;
    }

    // Helper method to get nested property path
    default Path<?> getPropertyPath(Root<?> root, String propertyPath) {
        String[] pathParts = propertyPath.split("\\.");
        Path<?> path = root;

        for (String part : pathParts) {
            path = path.get(part);
        }

        return path;
    }

    // QueryDSL support
    Optional<TAggregate> findOne(Predicate predicate);

    List<TAggregate> findAll(Predicate predicate);

    Page<TAggregate> findAll(Predicate predicate, Pageable pageable);

    List<TAggregate> findAll(Predicate predicate, Sort sort);

    long count(Predicate predicate);

    boolean exists(Predicate predicate);
}
