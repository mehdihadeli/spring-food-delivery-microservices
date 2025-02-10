package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import java.time.LocalDateTime;

public interface SoftDeleteBase {
    boolean isDeleted();

    void setDeleted(boolean deleted);

    LocalDateTime getDeletedDate();

    void setDeletedDate(LocalDateTime deletedDate);

    void setDeletedByObject(Object deletedBy);
}
