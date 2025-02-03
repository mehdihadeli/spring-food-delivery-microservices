package com.github.mehdihadeli.buildingblocks.jpa;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

// https://docs.spring.io/spring-data/jpa/reference/repositories/custom-implementations.html#repositories.customize-base-repository

public class ExtendedSimpleRepository<T, ID> extends SimpleJpaRepository<T, ID> {
    private final EntityManager entityManager;

    ExtendedSimpleRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        // Keep the EntityManager around to used from the newly introduced methods.
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        var result = super.save(entity);

        return result;
    }
}
