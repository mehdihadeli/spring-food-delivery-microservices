package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IEntityDataModelBase;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@FilterDef(
        name = "deletedFilter",
        parameters = @ParamDef(name = "deleted", type = boolean.class),
        applyToLoadByKey = true)
@Filter(name = "deletedFilter", condition = "is_deleted = :deleted")
@MappedSuperclass
public abstract class EntityDataModelBase<TId extends Serializable> implements IEntityDataModelBase<TId> {

    @Id
    @Column(nullable = false, unique = true)
    // @GeneratedValue(strategy = GenerationType.AUTO) // we generate id manually, but if we have Auto it can generate
    // id for uuid and long
    private TId id;

    @Column(name = "is_deleted", nullable = true)
    private boolean deleted = false;

    @Column(name = "deleted_date", nullable = true)
    private LocalDateTime deletedDate;

    @Column(name = "deleted_by", nullable = true)
    private TId deletedBy;

    // https://stackoverflow.com/questions/2572566/java-jpa-version-annotation
    // for handling optimistic concurrency
    @Version
    private int version;

    @Transient
    protected boolean isNew = false;

    protected EntityDataModelBase() {}

    // to consider new entity as `isNew=true`
    public void markAsNew() {
        this.isNew = true;
    }

    // for detecting, it is update or insert when we use `repository.save()`. if isNew is true repository uses
    // `entityManager.persist()` and if isNew is false repository uses `entityManager.merge()` on save.
    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    @Override
    public TId getId() {
        return id;
    }

    @Override
    public void setId(TId id) {
        this.id = id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }

    @Override
    public void setDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    @Override
    public TId getDeletedBy() {
        return deletedBy;
    }

    @Override
    public void setDeletedBy(TId deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public void setDeletedByObject(Object deletedBy) {
        this.deletedBy = (TId) deletedBy;
    }
}
