package com.github.mehdihadeli.catalogs.categories.data.entities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableEntityDataModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "categories")
public class CategoryDataModel extends AuditableEntityDataModel {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Nullable
    @Column(nullable = true)
    private String description;

    // Default constructor for JPA
    public CategoryDataModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
