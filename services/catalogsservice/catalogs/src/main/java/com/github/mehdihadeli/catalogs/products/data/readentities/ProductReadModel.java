package com.github.mehdihadeli.catalogs.products.data.readentities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableReadEntity;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.DimensionsVO;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.PriceVO;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.SizeVO;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

// MongoDB is schema-less by design, so it doesn't enforce schema constraints like relational databases do, we can
// still enforce constraints at the application level like uisng `@Nullable` because we don't have `column= not null`
@Document(collection = "products")
public class ProductReadModel extends AuditableReadEntity {
    private UUID productId;

    private UUID categoryId;
    private String name;

    @Nullable
    private String description;

    private PriceVO price;
    private SizeVO size;
    private DimensionsVO dimensions;
    private ProductStatus status;

    // Embedded list of variants
    private List<ProductVariantReadModel> variants = new ArrayList<>();

    // Embedded list of reviews
    private List<ProductReviewReadModel> reviews = new ArrayList<>();

    // Default constructor for deserialization and mongo
    public ProductReadModel() {}

    // Getters and Setters
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<ProductVariantReadModel> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantReadModel> variants) {
        this.variants = variants;
    }

    public List<ProductReviewReadModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReviewReadModel> reviews) {
        this.reviews = reviews;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
