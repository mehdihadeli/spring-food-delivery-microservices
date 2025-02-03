package com.github.mehdihadeli.buildingblocks.core.data;

import jakarta.persistence.Id;
import java.io.Serializable;

public class ReadEntityBase<TId extends Serializable> {
    @Id
    private TId id;

    public TId getId() {
        return id;
    }

    public void setId(TId id) {
        this.id = id;
    }
}
