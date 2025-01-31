package com.github.mehdihadeli.buildingblocks.abstractions.core.domain;

public interface IEntity<TId> {
    TId getId();

    void setId(TId id);
}
