package com.github.mehdihadeli.buildingblocks.core.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CriteriaQueryUtils {

    private CriteriaQueryUtils() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Find all entities matching the specification
     */
    public static <T> List<T> findAll(EntityManager entityManager, Class<T> entityClass, Specification<T> spec) {
        CriteriaQuery<T> query = createQuery(entityManager, entityClass, spec);
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find all entities matching the specification with sorting
     */
    public static <T> List<T> findAll(
            EntityManager entityManager, Class<T> entityClass, Specification<T> spec, Sort sort) {
        CriteriaQuery<T> query = createQuery(entityManager, entityClass, spec);
        addSorting(entityManager, query, entityClass, sort);
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find all entities matching the specification with pagination
     */
    public static <T> Page<T> findAll(
            EntityManager entityManager, Class<T> entityClass, Specification<T> spec, Pageable pageable) {
        CriteriaQuery<T> query = createQuery(entityManager, entityClass, spec);

        if (pageable.getSort().isSorted()) {
            addSorting(entityManager, query, entityClass, pageable.getSort());
        }

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        Long total = count(entityManager, entityClass, spec);

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }

    /**
     * Find a single result matching the specification
     */
    public static <T> Optional<T> findOne(EntityManager entityManager, Class<T> entityClass, Specification<T> spec) {
        CriteriaQuery<T> query = createQuery(entityManager, entityClass, spec);
        try {
            return Optional.ofNullable(
                    entityManager.createQuery(query).setMaxResults(1).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Count entities matching the specification
     */
    public static <T> Long count(EntityManager entityManager, Class<T> entityClass, Specification<T> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        query.select(cb.count(root));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Delete entity
     */
    public static <T> void delete(EntityManager entityManager, T entity) {
        T managedEntity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
        entityManager.remove(managedEntity);
    }

    /**
     * Delete all entity
     */
    public static <T> void deleteAll(EntityManager entityManager, Class<T> entityClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
        delete.from(entityClass);

        entityManager.createQuery(delete).executeUpdate();
    }

    /**
     * Delete entities matching the specification
     */
    public static <T> void delete(EntityManager entityManager, Class<T> entityClass, Specification<T> spec) {
        if (spec == null) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
            delete.from(entityClass);
            entityManager.createQuery(delete).executeUpdate();
            return;
        }

        // First get the IDs of records to delete using the specification
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> queryRoot = query.from(entityClass);
        query.select(queryRoot);
        query.where(spec.toPredicate(queryRoot, query, cb));

        List<T> entitiesToDelete = entityManager.createQuery(query).getResultList();

        // Then delete those entities
        if (!entitiesToDelete.isEmpty()) {
            CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
            Root<T> deleteRoot = delete.from(entityClass);

            // Create an IN clause with the IDs
            Path<?> idPath = deleteRoot.get("id"); // Assuming there's an id field
            List<?> ids = entitiesToDelete.stream()
                    .map(entity -> {
                        try {
                            return entityClass.getMethod("getId").invoke(entity);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to get ID from entity", e);
                        }
                    })
                    .collect(Collectors.toList());

            delete.where(idPath.in(ids));
            entityManager.createQuery(delete).executeUpdate();
        }
    }

    /**
     * Find all entities matching the specification with projections.
     */
    public static <T, R> List<R> findAllWithProjection(
            EntityManager entityManager,
            Class<T> entityClass,
            Class<R> resultClass,
            Specification<T> spec,
            List<String> fields) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> query = cb.createQuery(resultClass);
        Root<T> root = query.from(entityClass);

        List<Selection<R>> selections = fields.stream()
                .map(field -> (Selection<R>) getPropertyPath(root, field)) // Cast Path<?> to Selection<?>
                .toList();

        query.select(cb.construct(resultClass, selections.toArray(Selection[]::new)));

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Fetch entities using a batch size to avoid memory issues.
     */
    public static <T> List<T> findAllWithBatchSize(
            EntityManager entityManager, Class<T> entityClass, Specification<T> spec, int batchSize) {
        List<T> results = new ArrayList<>();
        CriteriaQuery<T> query = createQuery(entityManager, entityClass, spec);
        TypedQuery<T> typedQuery = entityManager.createQuery(query);

        for (int i = 0; ; i += batchSize) {
            typedQuery.setFirstResult(i);
            typedQuery.setMaxResults(batchSize);
            List<T> batchResults = typedQuery.getResultList();
            if (batchResults.isEmpty()) {
                break;
            }
            results.addAll(batchResults);
        }

        return results;
    }

    /**
     * Create a base criteria query with specification
     */
    private static <T> CriteriaQuery<T> createQuery(
            EntityManager entityManager, Class<T> entityClass, Specification<T> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root);

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        return query;
    }

    /**
     * Add sorting to a criteria query
     */
    private static <T> void addSorting(
            EntityManager entityManager, CriteriaQuery<T> query, Class<T> entityClass, Sort sort) {
        if (sort == null || !sort.isSorted()) {
            return;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<T> root = getRoot(query, entityClass);

        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : sort) {
            Path<?> path = getPropertyPath(root, sortOrder.getProperty());
            Order order = sortOrder.isAscending() ? cb.asc(path) : cb.desc(path);
            orders.add(order);
        }

        query.orderBy(orders);
    }

    /**
     * Get the root of the criteria query
     */
    private static <T> Root<T> getRoot(CriteriaQuery<?> query, Class<T> entityClass) {
        return (Root<T>) query.getRoots().stream()
                .filter(root -> root.getJavaType().equals(entityClass))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No root found for class: " + entityClass));
    }

    /**
     * Get a nested property path
     */
    private static Path<?> getPropertyPath(Root<?> root, String propertyPath) {
        String[] pathParts = propertyPath.split("\\.");
        Path<?> path = root;

        for (String part : pathParts) {
            path = path.get(part);
        }

        return path;
    }

    /**
     * Utility class for common specifications and predicates
     */
    public static class Specifications {

        /**
         * Equal predicate for any field
         */
        public static <T> Specification<T> equalTo(String field, Object value) {
            return (root, query, cb) -> {
                if (value == null) {
                    return null;
                }
                return cb.equal(getPath(root, field), value);
            };
        }

        /**
         * Not equal predicate
         */
        public static <T> Specification<T> notEqualTo(String field, Object value) {
            return (root, query, cb) -> {
                if (value == null) {
                    return null;
                }
                return cb.notEqual(getPath(root, field), value);
            };
        }

        /**
         * Like pattern matching
         */
        public static <T> Specification<T> like(String field, String pattern) {
            return (root, query, cb) -> {
                if (pattern == null) {
                    return null;
                }
                return cb.like(getPath(root, field), "%" + pattern + "%");
            };
        }

        /**
         * In list of values
         */
        public static <T> Specification<T> in(String field, Collection<?> values) {
            return (root, query, cb) -> {
                if (values == null || values.isEmpty()) {
                    return null;
                }
                return getPath(root, field).in(values);
            };
        }

        /**
         * Between range for comparable types
         */
        public static <T> Specification<T> between(String field, Comparable<?> start, Comparable<?> end) {
            return (root, query, cb) -> {
                if (start == null || end == null) {
                    return null;
                }
                return cb.between(getPath(root, field), (Comparable) start, (Comparable) end);
            };
        }

        /**
         * Greater than
         */
        public static <T> Specification<T> greaterThan(String field, Comparable<?> value) {
            return (root, query, cb) -> {
                if (value == null) {
                    return null;
                }
                return cb.greaterThan(getPath(root, field), (Comparable) value);
            };
        }

        /**
         * Less than
         */
        public static <T> Specification<T> lessThan(String field, Comparable<?> value) {
            return (root, query, cb) -> {
                if (value == null) {
                    return null;
                }
                return cb.lessThan(getPath(root, field), (Comparable) value);
            };
        }

        /**
         * Is null check
         */
        public static <T> Specification<T> isNull(String field) {
            return (root, query, cb) -> cb.isNull(getPath(root, field));
        }

        /**
         * Is not null check
         */
        public static <T> Specification<T> isNotNull(String field) {
            return (root, query, cb) -> cb.isNotNull(getPath(root, field));
        }

        /**
         * Date range specification
         */
        public static <T> Specification<T> dateRange(String field, LocalDateTime start, LocalDateTime end) {
            return (root, query, cb) -> {
                if (start != null && end != null) {
                    return cb.between(getPath(root, field), start, end);
                } else if (start != null) {
                    return cb.greaterThanOrEqualTo(getPath(root, field), start);
                } else if (end != null) {
                    return cb.lessThanOrEqualTo(getPath(root, field), end);
                }
                return null;
            };
        }

        /**
         * Join condition
         */
        public static <T> Specification<T> joinAndEqual(String joinField, String field, Object value) {
            return (root, query, cb) -> {
                Join<Object, Object> join = root.join(joinField);
                return cb.equal(join.get(field), value);
            };
        }

        /**
         * Exists subquery
         */
        public static <T> Specification<T> exists(String joinField, String field, Object value) {
            return (root, query, cb) -> {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<?> subRoot = subquery.from(root.get(joinField).getJavaType());
                subquery.select(cb.literal(1));
                subquery.where(cb.equal(subRoot.get(field), value));
                return cb.exists(subquery);
            };
        }

        private static <T> Path<T> getPath(Root<?> root, String field) {
            String[] parts = field.split("\\.");
            Path<T> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
    }

    /**
     * Specification builder for fluent API
     */
    public static class SpecificationBuilder<T> {
        private Specification<T> spec = Specification.where(null);

        public SpecificationBuilder<T> equalTo(String field, Object value) {
            return and(Specifications.equalTo(field, value));
        }

        public SpecificationBuilder<T> notEqualTo(String field, Object value) {
            return and(Specifications.notEqualTo(field, value));
        }

        public SpecificationBuilder<T> like(String field, String pattern) {
            return and(Specifications.like(field, pattern));
        }

        public SpecificationBuilder<T> in(String field, Collection<?> values) {
            return and(Specifications.in(field, values));
        }

        public SpecificationBuilder<T> between(String field, Comparable<?> start, Comparable<?> end) {
            return and(Specifications.between(field, start, end));
        }

        public SpecificationBuilder<T> greaterThan(String field, Comparable<?> value) {
            return and(Specifications.greaterThan(field, value));
        }

        public SpecificationBuilder<T> lessThan(String field, Comparable<?> value) {
            return and(Specifications.lessThan(field, value));
        }

        public SpecificationBuilder<T> isNull(String field) {
            return and(Specifications.isNull(field));
        }

        public SpecificationBuilder<T> isNotNull(String field) {
            return and(Specifications.isNotNull(field));
        }

        public SpecificationBuilder<T> dateRange(String field, LocalDateTime start, LocalDateTime end) {
            return and(Specifications.dateRange(field, start, end));
        }

        public SpecificationBuilder<T> joinAndEqual(String joinField, String field, Object value) {
            return and(Specifications.joinAndEqual(joinField, field, value));
        }

        public SpecificationBuilder<T> exists(String joinField, String field, Object value) {
            return and(Specifications.exists(joinField, field, value));
        }

        public SpecificationBuilder<T> and(Specification<T> specification) {
            spec = spec == null ? specification : spec.and(specification);
            return this;
        }

        public SpecificationBuilder<T> or(Specification<T> specification) {
            spec = spec == null ? specification : spec.or(specification);
            return this;
        }

        public Specification<T> build() {
            return spec;
        }
    }
}
