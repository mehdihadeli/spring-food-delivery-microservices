package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.AggregateRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CriteriaQueryBuilder;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomJpaRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.github.mehdihadeli.buildingblocks.core.exceptions.ConflictException;
import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Enhanced base repository supporting both specification types
public abstract class AggregateBaseRepository<
                TAggregate extends Aggregate<TAggregateId>,
                TDataModelEntity extends EntityDataModelBase<TDataModelEntityId>,
                TAggregateId extends IdentityId<TDataModelEntityId>,
                TDataModelEntityId extends Serializable>
        implements AggregateRepository<TAggregate, TDataModelEntity, TAggregateId, TDataModelEntityId> {
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

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public CustomJpaRepository<TDataModelEntity, TDataModelEntityId> getCustomJpaRepository() {
        return customJpaRepository;
    }

    // Regular CRUD operations
    @Override
    public TAggregate add(TAggregate aggregate) {
        if (customJpaRepository.existsById(aggregate.getId().id())) {
            throw new ConflictException("Entity with ID " + aggregate.getId() + " already exists.");
        }
        TDataModelEntity entity = toDataModel(aggregate);
        entity.markAsNew();

        customJpaRepository.save(entity);
        return toAggregate(entity);
    }

    @Override
    public void update(TAggregate aggregate) {
        var entity = customJpaRepository.findById(aggregate.getId().id());
        if (entity.isEmpty()) {
            throw new NotFoundException(String.format("DataModel with ID %s not found.", aggregate.getId()));
        }
        var dataModel = toDataModel(aggregate, entity.get());
        customJpaRepository.save(dataModel);
    }

    @Override
    public TAggregate save(TAggregate aggregate) {
        TDataModelEntity entity = toDataModel(aggregate);
        entity = customJpaRepository.save(entity);
        return toAggregate(entity);
    }

    @Override
    public Optional<TAggregate> findById(TAggregateId id) {
        applySoftDeleteFilter();
        return customJpaRepository.findById(id.id()).map(this::toAggregate);
    }

    @Override
    public void deleteById(TAggregateId id) {
        // Fetch the entity first to trigger the @PreRemove callback
        findById(id).ifPresent(this::delete);
    }

    @Override
    public void delete(TAggregate aggregate) {
        customJpaRepository.deleteById(aggregate.getId().id());
    }

    // Methods using JPA Specification
    @Override
    public List<TAggregate> findAll(Specification<TDataModelEntity> spec) {
        applySoftDeleteFilter();
        return customJpaRepository.findAll(spec).stream().map(this::toAggregate).toList();
    }

    @Override
    public Optional<TAggregate> findOne(Specification<TDataModelEntity> spec) {
        applySoftDeleteFilter();
        return customJpaRepository.findOne(spec).map(this::toAggregate);
    }

    @Override
    public Page<TAggregate> findAll(Specification<TDataModelEntity> spec, Pageable pageable) {
        applySoftDeleteFilter();
        return customJpaRepository.findAll(spec, pageable).map(this::toAggregate);
    }

    // Methods using QBE
    @Override
    public List<TAggregate> findAllByExample(TDataModelEntity exampleEntity) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findAll(example).stream()
                .map(this::toAggregate)
                .toList();
    }

    @Override
    public Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findOne(example).map(this::toAggregate);
    }

    @Override
    public Page<TAggregate> findAllByExample(TDataModelEntity exampleEntity, Pageable pageable) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity);
        return customJpaRepository.findAll(example, pageable).map(this::toAggregate);
    }

    @Override
    public List<TAggregate> findAllByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findAll(example).stream()
                .map(this::toAggregate)
                .toList();
    }

    @Override
    public Optional<TAggregate> findOneByExample(TDataModelEntity exampleEntity, ExampleMatcher matcher) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findOne(example).map(this::toAggregate);
    }

    @Override
    public Page<TAggregate> findAllByExample(
            TDataModelEntity exampleEntity, ExampleMatcher matcher, Pageable pageable) {
        applySoftDeleteFilter();
        Example<TDataModelEntity> example = Example.of(exampleEntity, matcher);
        return customJpaRepository.findAll(example, pageable).map(this::toAggregate);
    }

    // CriteriaQuery support
    @Override
    public List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        applySoftDeleteFilter();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().map(this::toAggregate).toList();
    }

    @Override
    public Page<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Pageable pageable) {
        applySoftDeleteFilter();
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

    @Override
    public List<TAggregate> findByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder, Sort sort) {
        applySoftDeleteFilter();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TDataModelEntity> query = queryBuilder.build(cb, this.entityClass);

        // Apply sorting
        var root = query.getRoots().iterator().next();
        query.orderBy(createOrders(cb, root, sort));

        TypedQuery<TDataModelEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().map(this::toAggregate).toList();
    }

    @Override
    public Optional<TAggregate> findOneByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        applySoftDeleteFilter();
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

    @Override
    public long countByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        applySoftDeleteFilter();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TDataModelEntity> root = query.from(this.entityClass);
        query.select(cb.count(root));
        queryBuilder.applyPredicates(cb, root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public boolean existsByCriteria(CriteriaQueryBuilder<TDataModelEntity> queryBuilder) {
        applySoftDeleteFilter();
        return countByCriteria(queryBuilder) > 0;
    }

    // QueryDSL support
    @Override
    public Optional<TAggregate> findOne(Predicate predicate) {
        applySoftDeleteFilter();
        return customJpaRepository.findOne(predicate).map(this::toAggregate);
    }

    @Override
    public List<TAggregate> findAll(Predicate predicate) {
        applySoftDeleteFilter();
        Iterable<TDataModelEntity> entities = customJpaRepository.findAll(predicate);
        List<TAggregate> aggregates = new ArrayList<>();
        entities.forEach(entity -> aggregates.add(toAggregate(entity)));
        return aggregates;
    }

    @Override
    public Page<TAggregate> findAll(Predicate predicate, Pageable pageable) {
        applySoftDeleteFilter();
        return customJpaRepository.findAll(predicate, pageable).map(this::toAggregate);
    }

    @Override
    public List<TAggregate> findAll(Predicate predicate, Sort sort) {
        applySoftDeleteFilter();
        Iterable<TDataModelEntity> entities = customJpaRepository.findAll(predicate, sort);
        List<TAggregate> aggregates = new ArrayList<>();
        entities.forEach(entity -> aggregates.add(toAggregate(entity)));
        return aggregates;
    }

    @Override
    public long count(Predicate predicate) {
        applySoftDeleteFilter();
        return customJpaRepository.count(predicate);
    }

    @Override
    public boolean exists(Predicate predicate) {
        applySoftDeleteFilter();
        return customJpaRepository.exists(predicate);
    }

    private void applySoftDeleteFilter() {
        // for each request and scoped life-time we have a new EntityManager and session
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedFilter");
        filter.setParameter("deleted", false);
    }
}
