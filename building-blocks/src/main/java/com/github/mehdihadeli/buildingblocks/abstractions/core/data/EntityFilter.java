package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface EntityFilter<T> {
    Predicate apply(Root<T> root, CriteriaBuilder criteriaBuilder);
}
