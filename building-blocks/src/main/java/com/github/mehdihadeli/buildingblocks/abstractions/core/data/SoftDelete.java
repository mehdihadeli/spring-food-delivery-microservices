package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

public interface SoftDelete<TId> extends SoftDeleteBase {
    TId getDeletedBy();

    void setDeletedBy(TId deletedBy);
}
