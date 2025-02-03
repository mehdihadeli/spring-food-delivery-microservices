package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CriteriaQueryBuilder;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomJpaRepository;
import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class RepositoryBase<TEntity, TID> {
    private final EntityManager entityManager;
    private final CustomJpaRepository<TEntity, TID> customJpaRepository;
    private final Class<TEntity> entityClass;

    // JpaEntityInformation will pass dynamically with reflection through `JpaRepositoryFactoryBean<> and its base
    // `RepositoryFactoryBeanSupport` inside `getRepositoryInformation` and its `getRepositoryMetadata` call internally.
    // then find implementation for repository interface in `RepositoryFactorySupport.getRepository` which is
    // `SimpleJpaRepository` as default
    protected RepositoryBase(EntityManager entityManager, CustomJpaRepository<TEntity, TID> customJpaRepository) {
        var genericArguments = ReflectionUtils.getGenericTypeArguments(this.getClass());

        this.entityManager = entityManager;
        this.customJpaRepository = customJpaRepository;
        this.entityClass = (Class<TEntity>) genericArguments[0];
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CustomJpaRepository<TEntity, TID> getCustomJpaRepository() {
        return customJpaRepository;
    }

    // CriteriaQuery support
    public List<TEntity> findByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().toList();
    }

    public Page<TEntity> findByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TEntity> countRoot = countQuery.from(this.entityClass);
        countQuery.select(cb.count(countRoot));
        queryBuilder.applyPredicates(cb, countRoot, countQuery);
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // Get paginated results
        CriteriaQuery<TEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<TEntity> content = typedQuery.getResultList().stream().toList();

        return new PageImpl<>(content, pageable, totalCount);
    }

    public List<TEntity> findByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = queryBuilder.build(cb, this.entityClass);

        // Apply sorting
        var root = query.getRoots().iterator().next();
        query.orderBy(createOrders(cb, root, sort));

        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().toList();
    }

    public Optional<TEntity> findOneByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = queryBuilder.build(cb, this.entityClass);
        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        List<TEntity> results = typedQuery.getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    public long countByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TEntity> root = query.from(this.entityClass);
        query.select(cb.count(root));
        queryBuilder.applyPredicates(cb, root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    public boolean existsByCriteria(CriteriaQueryBuilder<TEntity> queryBuilder) {
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
}
