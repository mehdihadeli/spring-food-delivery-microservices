package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IAuditableEntityDataModelBase;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

// https://docs.spring.io/spring-data/jpa/reference/auditing.html

// - If our application follows standard UUID handling with @GeneratedValue, Persistable<ID> is not necessary. If we
// generate UUIDs manually and need to override default behavior, consider implementing Persistable<ID>.
//  each level in the inheritance hierarchy that contains persistent fields needs the annotation with @MappedSuperclass,
// Any non-entity class whose fields should be persisted in a child entity needs @MappedSuperclass.
@MappedSuperclass
public abstract class AuditableEntityDataModelBase<TId extends Serializable> extends EntityDataModelBase<TId>
        implements IAuditableEntityDataModelBase<TId> {

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @CreatedBy
    @Column(nullable = false)
    private TId createdBy;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    @Column(nullable = true)
    private TId lastModifiedBy;

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public TId getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(TId createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public void setCreatedByObject(Object createdBy) {
        this.createdBy = (TId) createdBy;
    }

    @Override
    public Optional<LocalDateTime> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @Override
    public void setLastModifiedDate(@Nullable LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public Optional<TId> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(@Nullable TId lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public void setLastModifiedByObject(Object lastModifiedBy) {
        this.lastModifiedBy = (TId) lastModifiedBy;
    }
}
