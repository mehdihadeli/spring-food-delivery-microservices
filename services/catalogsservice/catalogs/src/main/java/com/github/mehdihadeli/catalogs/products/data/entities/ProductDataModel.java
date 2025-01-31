package com.github.mehdihadeli.catalogs.products.data.entities;

import com.github.mehdihadeli.catalogs.products.data.valueobjects.DimensionsVO;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.PriceVO;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.SizeVO;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.buildingblocks.core.data.AuditableAggregateDataModel;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.util.*;

@Entity
@Table(name = "products")
public class ProductDataModel extends AuditableAggregateDataModel {
    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    @Nullable
    private String description; // Nullable by default since `nullable = false` is not specified

    @Column(nullable = false)
    @Embedded
    private PriceVO price;

    @Column(nullable = false)
    @Embedded
    private SizeVO size;

    @Column(nullable = false)
    @Embedded
    private DimensionsVO dimensions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    // https://www.callicoder.com/hibernate-spring-boot-jpa-one-to-many-mapping-example/
    // https://www.callicoder.com/hibernate-spring-boot-jpa-one-to-one-mapping-example/
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductVariantDataModel> variants = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductReviewDataModel> reviews = new ArrayList<>();

    // Default constructor for JPA
    public ProductDataModel() {}

    // All getters and setters

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public PriceVO getPrice() {
        return price;
    }

    public void setPrice(PriceVO price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public SizeVO getSize() {
        return size;
    }

    public void setSize(SizeVO size) {
        this.size = size;
    }

    public DimensionsVO getDimensions() {
        return dimensions;
    }

    public void setDimensions(DimensionsVO dimensions) {
        this.dimensions = dimensions;
    }

    public Set<ProductVariantDataModel> getVariants() {
        return variants;
    }

    public void setVariants(Set<ProductVariantDataModel> variants) {
        this.variants = variants;
    }

    public List<ProductReviewDataModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReviewDataModel> reviews) {
        this.reviews = reviews;
    }
}
