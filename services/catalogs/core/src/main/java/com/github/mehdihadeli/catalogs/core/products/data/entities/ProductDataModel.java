package com.github.mehdihadeli.catalogs.core.products.data.entities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableAggregateDataModel;
import com.github.mehdihadeli.catalogs.core.categories.data.entities.CategoryDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.DimensionsVO;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.PriceVO;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.SizeVO;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import jakarta.persistence.*;
import java.util.*;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "products")
public class ProductDataModel extends AuditableAggregateDataModel {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    @Nullable
    private String description; // Nullable by default since `nullable = false` is not specified

    // - By default, JPA does not prepend the embedded field’s name (e.g., price_ for PriceVO). Instead, it directly
    // uses the field names inside PriceVO, leading to amount and currency instead of price_amount and price_currency.
    // The @AttributeOverrides annotation explicitly tells JPA how to map these fields.
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    @Column(nullable = false)
    @Embedded
    private PriceVO price;

    @AttributeOverrides({
        @AttributeOverride(name = "size", column = @Column(name = "size_size")),
        @AttributeOverride(name = "unit", column = @Column(name = "size_unit"))
    })
    @Column(nullable = false)
    @Embedded
    private SizeVO size;

    @AttributeOverrides({
        @AttributeOverride(name = "width", column = @Column(name = "dimensions_width")),
        @AttributeOverride(name = "height", column = @Column(name = "dimensions_height")),
        @AttributeOverride(name = "depth", column = @Column(name = "dimensions_depth"))
    })
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

    @Column(nullable = false, name = "category_id")
    private UUID categoryId;

    // force `fk` on category_id column but category is optional in insert and will get during fetch with lazy loading
    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private CategoryDataModel category;

    public CategoryDataModel getCategory() {
        return category;
    }

    public void setCategory(CategoryDataModel category) {
        this.category = category;
    }

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
