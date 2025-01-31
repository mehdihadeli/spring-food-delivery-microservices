package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

public interface IAuditableAggregateDataModelBase<TId>
        extends IAuditableEntityDataModelBase<TId>, IAggregateDataModelBase<TId> {}
