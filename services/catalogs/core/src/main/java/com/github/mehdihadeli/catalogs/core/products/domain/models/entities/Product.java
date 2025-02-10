package com.github.mehdihadeli.catalogs.core.products.domain.models.entities;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Money;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.core.domain.Aggregate;
import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.*;
import com.github.mehdihadeli.catalogs.core.products.exceptions.DuplicateReviewException;
import com.github.mehdihadeli.catalogs.core.products.exceptions.DuplicateSkuException;
import com.github.mehdihadeli.catalogs.core.products.exceptions.InvalidStatusTransitionException;
import com.github.mehdihadeli.catalogs.core.products.exceptions.ReviewNotFoundException;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain.ProductCreated;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproductreview.v1.events.domain.ProductReviewCreated;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproductvariant.v1.events.domain.ProductVariantCreated;
import com.github.mehdihadeli.catalogs.core.products.features.removingproductvariant.v1.events.domain.ProductVariantRemoved;
import com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1.events.domain.ProductDetailsUpdated;
import com.github.mehdihadeli.catalogs.core.products.features.verifyingproductreview.v1.events.domain.ProductReviewVerified;
import java.util.*;
import org.springframework.lang.Nullable;

// Aggregates should reference other aggregates by their identity (ID), not by direct object composition, to
// maintain autonomy and avoid deep object graphs.
public class Product extends Aggregate<ProductId> {
    private final Set<ProductVariant> variants;
    private final List<ProductReview> reviews;
    private CategoryId categoryId;
    private Name name;
    private ProductStatus status;

    @Nullable
    private Description description;

    private Price price;
    private Size size;
    private Dimensions dimensions;

    private Product(
            ProductId productId,
            CategoryId categoryId,
            Name name,
            Price price,
            ProductStatus status,
            Dimensions dimensions,
            Size size,
            @Nullable List<ProductReview> reviews,
            @Nullable Set<ProductVariant> initialVariants,
            @Nullable Description description) {

        super.setId(productId);
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.status = status;
        this.dimensions = dimensions;
        this.size = size;
        this.description = description;
        this.variants = initialVariants == null ? new HashSet<>() : new HashSet<>(initialVariants);
        this.reviews = reviews == null ? new ArrayList<>() : new ArrayList<>(reviews);
    }

    public static Product create(
            ProductId productId,
            CategoryId categoryId,
            Name name,
            Price price,
            ProductStatus status,
            Dimensions dimensions,
            Size size,
            @Nullable List<ProductReview> reviews,
            @Nullable Set<ProductVariant> initialVariants,
            @Nullable Description description)
            throws DuplicateSkuException, IllegalArgumentException {

        // factory method serves as the clear entry point for domain validation
        if (initialVariants != null) {
            validateVariant(productId, initialVariants);
        }

        var product = new Product(
                notBeNull(productId, "productId"),
                notBeNull(categoryId, "categoryId"),
                notBeNull(name, "name"),
                notBeNull(price, "price"),
                status,
                notBeNull(dimensions, "dimensions"),
                notBeNull(size, "size"),
                reviews,
                initialVariants,
                description);

        // Add the ProductCreated domain event with the complete product details
        product.addDomainEvent(new ProductCreated(
                productId, categoryId, name, price, status, dimensions, size, reviews, initialVariants, description));

        return product;
    }

    // Validation Methods
    private static void validateVariant(ProductId productId, Set<ProductVariant> initialVariants)
            throws DuplicateSkuException {
        Set<String> skus = new HashSet<>();
        for (ProductVariant variant : initialVariants) {
            if (!skus.add(variant.getSku().value())) {
                throw new DuplicateSkuException(variant.getSku(), productId);
            }
        }
    }

    // Variant Management
    public void addVariant(VariantId variantId, SKU sku, Money price, Stock stock, Color color)
            throws DuplicateSkuException {

        validateVariantSku(sku);
        ProductVariant variant = new ProductVariant(variantId, sku, price, stock, color);
        variants.add(variant);

        this.addDomainEvent(new ProductVariantCreated(
                this.getId(),
                variant.getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getStock(),
                variant.getColor()));
    }

    public void removeVariant(VariantId variantId) throws DomainException {
        if (variants.size() <= 1) {
            throw new DomainException("Cannot remove last variant from product");
        }
        variants.removeIf(variant -> variant.getId().equals(variantId));

        this.addDomainEvent(new ProductVariantRemoved(this.getId(), variantId));
    }

    // Review Management
    public void addReview(ProductReviewId reviewId, CustomerId customerId, Rating rating, Comment comment)
            throws DuplicateReviewException {

        validateCustomerReview(customerId);
        ProductReview review = new ProductReview(reviewId, customerId, rating, comment);
        reviews.add(review);

        // Raise the domain event for review creation
        this.addDomainEvent(new ProductReviewCreated(
                this.getId(), review.getId(), review.getCustomerId(), review.getRating(), review.getComment()));
    }

    public void updateReviewStatus(ProductReviewId reviewId, ReviewStatus newStatus) throws ReviewNotFoundException {
        findReview(reviewId).updateStatus(newStatus);
    }

    public void verifyReview(ProductReviewId reviewId) throws ReviewNotFoundException {
        findReview(reviewId).markAsVerified();

        // Raise the domain event
        this.addDomainEvent(new ProductReviewVerified(this.getId(), reviewId));
    }

    public void updateProductDetails(
            Name newName,
            Price newPrice,
            Dimensions newDimensions,
            Size newSize,
            @Nullable Description newDescription) {
        notBeNull(newName, "newName");
        notBeNull(newPrice, "newPrice");
        notBeNull(newDimensions, "newDimensions");
        notBeNull(newSize, "newSize");

        this.name = newName;
        this.description = newDescription;
        this.price = newPrice;
        this.dimensions = newDimensions;
        this.size = newSize;

        this.addDomainEvent(new ProductDetailsUpdated(getId(), name, price, newDimensions, newSize, description));
    }

    public void activate() throws DomainException {
        if (this.status == ProductStatus.Active) {
            throw new DomainException("Product is already active.");
        }
        this.status = ProductStatus.Active;

        // domain event
    }

    public void deactivate() throws DomainException {
        if (this.status == ProductStatus.Inactive) {
            throw new DomainException("Product is already inactive.");
        }
        this.status = ProductStatus.Inactive;

        // domain event
    }

    public void updateStatus(ProductStatus newStatus) throws InvalidStatusTransitionException {
        validateStatusTransition(newStatus);
        this.status = newStatus;
    }

    public void changeCategory(CategoryId newCategoryId) {
        notBeNull(newCategoryId, "newCategoryId");
        this.categoryId = newCategoryId;

        // domain event
    }

    public void adjustPriceByPercentage(double percentage) {
        if (percentage == 0) {
            return; // No adjustment needed
        }
        this.price = this.price.adjustByPercentage(percentage);

        // domain event
    }

    // Validation Methods
    private void validateVariantSku(SKU sku) throws DuplicateSkuException {
        ValidationUtils.notBeNull(sku, "sku");
        ValidationUtils.notBeNull(this.getId(), "id");
        boolean skuExists =
                this.variants.stream().anyMatch(variant -> variant.getSku().equals(sku));
        if (skuExists) {
            throw new DuplicateSkuException(sku, this.getId());
        }
    }

    private void validateCustomerReview(CustomerId customerId) throws DuplicateReviewException {
        notBeNull(customerId, "customerId");
        boolean hasReviewed =
                reviews.stream().anyMatch(review -> review.getCustomerId().equals(customerId));
        if (hasReviewed) {
            throw new DuplicateReviewException(customerId, getId());
        }
    }

    private void validateStatusTransition(ProductStatus newStatus) throws InvalidStatusTransitionException {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(status, newStatus);
        }
    }

    private ProductReview findReview(ProductReviewId reviewId) throws ReviewNotFoundException {
        notBeNull(reviewId, "reviewId");
        return reviews.stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new ReviewNotFoundException(reviewId, getId()));
    }

    // Getters
    public CategoryId getCategoryId() {
        return categoryId;
    }

    public Name getName() {
        return name;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Price getPrice() {
        return price;
    }

    public Size getSize() {
        return size;
    }

    public Optional<Description> getDescription() {
        return Optional.ofNullable(description);
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public Set<ProductVariant> getVariants() {
        return Collections.unmodifiableSet(variants);
    }

    public List<ProductReview> getReviews() {
        return Collections.unmodifiableList(reviews);
    }
}
