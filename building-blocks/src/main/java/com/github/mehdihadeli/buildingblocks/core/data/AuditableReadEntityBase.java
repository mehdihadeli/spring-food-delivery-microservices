package com.github.mehdihadeli.buildingblocks.core.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

public class AuditableReadEntityBase<TId extends Serializable> extends ReadEntityBase<TId> {

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private TId createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    private TId lastModifiedBy;

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public TId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(TId createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public TId getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(TId lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
