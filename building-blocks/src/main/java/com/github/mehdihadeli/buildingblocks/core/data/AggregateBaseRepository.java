package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CriteriaQueryBuilder;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomJpaRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.EntityMapper;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.github.mehdihadeli.buildingblocks.core.exceptions.ConflictException;
import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

// Enhanced base repository supporting both specification types
public abstract class AggregateBaseRepository<
                TAggregate extends Aggregate<TAggregateId>,
                TDataModelEntity extends EntityDataModelBase<TDataModelEntityId>,
                TAggregateId extends IdentityId<TDataModelEntityId>,
                TDataModelEntityId extends Serializable>
        implements EntityMapper<TAggregate, TDataModelEntity> {
    private final EntityManager entityManager;
    private final CustomJpaRepository<TDataModelEntity, TDataModelEntityId> customJpaRepository;
    private final Class<TDataModelEntity> entityClass;

    // JpaEntityInformation will pass dynamically with reflection through `JpaRepositoryFactoryBean<> and its base
    // `RepositoryFactoryBeanSupport` inside `getRepositoryInformation` and its `getRepositoryMetadata` call internally.
    // then find implementation for repository interface in `RepositoryFactorySupport.getRepository` which is
    // `SimpleJpaRepository` as default
    protected AggregateBaseRepository(
            EntityManager entityManager,
            CustomJpaRepository<TDataModelEntity, TDataModelEntityId> customJpaRepository) {
        var genericArguments = ReflectionUtils.getGenericTypeArguments(this.getClass());

        this.entityManager = entityManager;
        this.customJpaRepository = customJpaRepository;
        this.entityClass = (Class<TDataModelEntity>) genericArguments[1];
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected CustomJpaRepository<TDataModelEntity, TDataModelEntityId> getCustomJpaRepository() {
        return customJpaRepository;
    }

    // Regular CRUD operations
    public TAggregate add(TAggregate aggregate) {
        if (customJpaRepository.existsById(aggregate.getId().id())) {
            throw new ConflictException("Entity with ID " + aggregate.getId() + " already exists.");
        }
        TDataModelEntity entity = toDataModel(aggregate);
        entity.markAsNew();

        customJpaRepository.save(entity);
        return toAggregate(entity);
    }

    public void update(TAggregate aggregate) {
        var entity = customJpaRepository.findById(aggregate.getId().id());
        if (entity.isEmpty()) {
            throw new NotFoundException(String.format("DataModel with ID %s not found.", aggregate.getId()));
        }
        var dataModel = toDataModel(aggregate, entity.get());
        customJpaRepository.save(dataModel);
    }

    public TAggregate save(TAggregate aggregate) {
        TDataModelEntity entity = toDataModel(aggregate);
        entity = customJpaRepository.save(entity);
        return toAggregate(entity);
    }

    public Optional<TAggregate> findById(TAggregateId id) {
        return customJpaRepository.findById(id.id()).map(this::toAggregate);
    }

    public void deleteById(TAggregateId id) {
        customJpaRepository.deleteById(id.id());
    }

    // Methods using JPA Specification
    public List<TAggregate> findAll(Specification<TDataModelEntity> spec) {
        return customJpaRepository.findAll(spec).stream().map(this::toAggregate).toList();
    }

    public Optional<TAggregate> findOne(Specification<TDataModelEntity> spec) {
        return customJpaRepository.findOne(spec).map(this::toAggregate);
    }

    public Page<TAggregate> findAll(Specification<TDataModelEntity> spec, Pageable pageable) {
        return customJpaRepository.findAll(spec, pageable).map(this::toAggregate);
    }

    // Methods using QBE
    public List<TAggregate> findAllByExample(TDataModelEntity exampleEntity) {
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findAll(example).stream()
                .map(this::toAggregate)
                .toList();
    }

    public Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity) {
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findOne(example).map(this::toAggregate);
    }

    public Page<TAggregate> findAllByExample(TDataModelEntity exampleEntity, Pageable pageable) {
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findAll(example, pageable).map(this::toAggregate);
    }

    public List<TAggregate> findAllByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher) {
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findAll(example).stream()
                .map(this::toAggregate)
                .toList();
    }

    public Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher) {
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findOne(example).map(this::toAggregate);
    }

    public Page<TAggregate> findAllByExample(
            TDataModelEntity exampleEntity, ExampleMatcher matcher, Pageable pageable) {
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findAll(example, pageable).map(this::toAggregate);
    }

    // CriteriaQuery support
    public List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().map(this::toAggregate).toList();
    }

    public Page<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TDataModelEntity> countRoot = countQuery.from(this.entityClass);
        countQuery.select(cb.count(countRoot));
        queryBuilder.applyPredicates(cb, countRoot, countQuery);
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // Get paginated results
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<TAggregate> content =
                typedQuery.getResultList().stream().map(this::toAggregate).toList();

        return new PageImpl<>(content, pageable, totalCount);
    }

    public List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);

        // Apply sorting
        var root = query.getRoots().iterator().next();
        query.orderBy(createOrders(cb, root, sort));

        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().map(this::toAggregate).toList();
    }

    public Optional<TAggregate> findOneByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        List<TDataModelEntity> results = typedQuery.getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toAggregate(results.get(0)));
    }

    public long countByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TDataModelEntity> root = query.from(this.entityClass);
        query.select(cb.count(root));
        queryBuilder.applyPredicates(cb, root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    public boolean existsByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        return countByCriteria(queryBuilder) > 0;
    }

    // Helper method to create Sort.Order into JPA Order
    private List<jakarta.persistence.criteria.Order> createOrders(CriteriaBuilder cb, Root<?> root, Sort sort) {
        List<Order> orders = new ArrayList<>();

        for (Sort.Order sortOrder : sort) {
            Path<?> path = getPropertyPath(root, sortOrder.getProperty());
            orders.add(sortOrder.isAscending() ? cb.asc(path) : cb.desc(path));
        }

        return orders;
    }

    // Helper method to get nested property path
    private jakarta.persistence.criteria.Path<?> getPropertyPath(Root<?> root, String propertyPath) {
        String[] pathParts = propertyPath.split("\\.");
        jakarta.persistence.criteria.Path<?> path = root;

        for (String part : pathParts) {
            path = path.get(part);
        }

        return path;
    }

    // QueryDSL support
    public Optional<TAggregate> findOne(Predicate predicate) {
        return customJpaRepository.findOne(predicate).map(this::toAggregate);
    }

    public List<TAggregate> findAll(Predicate predicate) {
        Iterable<TDataModelEntity> entities = customJpaRepository.findAll(predicate);
        List<TAggregate> aggregates = new ArrayList<>();
        entities.forEach(entity -> aggregates.add(toAggregate(entity)));
        return aggregates;
    }

    public Page<TAggregate> findAll(Predicate predicate, Pageable pageable) {
        return customJpaRepository.findAll(predicate, pageable).map(this::toAggregate);
    }

    public List<TAggregate> findAll(Predicate predicate, Sort sort) {
        Iterable<TDataModelEntity> entities = customJpaRepository.findAll(predicate, sort);
        List<TAggregate> aggregates = new ArrayList<>();
        entities.forEach(entity -> aggregates.add(toAggregate(entity)));
        return aggregates;
    }

    public long count(Predicate predicate) {
        return customJpaRepository.count(predicate);
    }

    public boolean exists(Predicate predicate) {
        return customJpaRepository.exists(predicate);
    }
}
