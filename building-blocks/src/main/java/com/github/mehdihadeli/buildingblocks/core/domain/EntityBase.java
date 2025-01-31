package com.github.mehdihadeli.buildingblocks.core.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IEntity;

public abstract class EntityBase<TId> implements IEntity<TId> {

    private TId id;

    protected EntityBase() {}

    @Override
    public TId getId() {
        return id;
    }

    @Override
    public void setId(TId id) {
        this.id = id;
    }
}
