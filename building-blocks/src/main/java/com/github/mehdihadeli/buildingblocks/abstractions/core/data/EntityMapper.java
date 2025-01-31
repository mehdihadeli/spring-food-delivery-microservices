package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EntityMapper<TAggregate, TEntity> {
    TAggregate toAggregate(TEntity entity);

    TEntity toDataModel(TAggregate aggregate);

    TEntity toDataModel(TAggregate aggregate, TEntity entity);
}
