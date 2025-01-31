package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import org.springframework.data.domain.Persistable;

public interface IEntityDataModelBase<TId> extends SoftDelete<TId>, Persistable<TId> {
    TId getId();

    void setId(TId id);

    int getVersion();

    void setVersion(int version);
}
