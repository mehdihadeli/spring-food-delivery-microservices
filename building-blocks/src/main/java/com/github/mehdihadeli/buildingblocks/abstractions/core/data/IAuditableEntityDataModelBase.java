package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.lang.Nullable;

public interface IAuditableEntityDataModelBase<TId> extends IEntityDataModelBase<TId> {
    LocalDateTime getCreatedDate();

    void setCreatedDate(LocalDateTime createdDate);

    TId getCreatedBy();

    void setCreatedBy(TId createdBy);

    void setCreatedByObject(Object createdBy);

    Optional<LocalDateTime> getLastModifiedDate();

    void setLastModifiedDate(@Nullable LocalDateTime lastModifiedDate);

    Optional<TId> getLastModifiedBy();

    void setLastModifiedBy(@Nullable TId lastModifiedBy);

    void setLastModifiedByObject(Object lastModifiedBy);
}
