package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IAggregateBase;

public interface IAggregateDataModelBase<TId> extends IAggregateBase, IEntityDataModelBase<TId> {}
