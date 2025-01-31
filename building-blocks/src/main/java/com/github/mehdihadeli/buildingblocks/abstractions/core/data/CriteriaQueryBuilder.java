package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

// Helper interface for building criteria queries
@FunctionalInterface
public interface CriteriaQueryBuilder<T> {
    CriteriaQuery<T> build(CriteriaBuilder cb, Class<T> entityClass);

    default void applyPredicates(CriteriaBuilder cb, Root<T> root, CriteriaQuery<?> query) {
        CriteriaQuery<T> typedQuery = build(cb, (Class<T>) root.getModel().getJavaType());
        if (typedQuery.getRestriction() != null) {
            query.where(typedQuery.getRestriction());
        }
        if (!typedQuery.getGroupList().isEmpty()) {
            query.groupBy(typedQuery.getGroupList());
        }
    }
}
