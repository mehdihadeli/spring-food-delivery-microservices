package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import java.time.LocalDateTime;

public interface SoftDelete<TId> {
    boolean isDeleted();

    void setDeleted(boolean deleted);

    LocalDateTime getDeletedDate();

    void setDeletedDate(LocalDateTime deletedDate);

    TId getDeletedBy();

    void setDeletedBy(TId deletedBy);

    void setDeletedByObject(Object deletedBy);
}
