package com.github.mehdihadeli.catalogs.products.domain.models.entities;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.domain.EntityBase;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Comment;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.CustomerId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductReviewId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Rating;
import java.util.Objects;

/**
 * Represents a product review entity in the e-commerce system.
 * Each review is uniquely identified and contains customer feedback for a specific product.
 */
public class ProductReview extends EntityBase<ProductReviewId> {
    private final CustomerId customerId;

    private Rating rating;
    private Comment comment;

    /**
     * Indicates if the review has been verified
     * (e.g., confirmed purchase, moderator approved)
     */
    private boolean verified;

    /**
     * List of helpful votes received from other customers.
     */
    private int helpfulVotes;

    /**
     * Current status of the review.
     */
    private ReviewStatus status;

    public ProductReview(ProductReviewId reviewId, CustomerId customerId, Rating rating, Comment comment) {
        this.setId(notBeNull(reviewId, "reviewId"));

        this.rating = notBeNull(rating, "rating");
        this.comment = notBeNull(comment, "comment");
        this.customerId = notBeNull(customerId, "customerId");
        this.status = ReviewStatus.PENDING;
        this.verified = false;
        this.helpfulVotes = 0;
    }

    // Private default constructor for Jackson serializer
    private ProductReview() {
        this.customerId = null;
    }

    /**
     * Updates the review content while maintaining an audit trail.
     *
     * @param newRating Updated rating value
     * @param newComment Updated comment text
     */
    public void updateReview(Rating newRating, Comment newComment) {
        this.rating = notBeNull(newRating, "rating");
        this.comment = notBeNull(newComment, "comment");
    }

    /**
     * Marks the review as verified after purchase confirmation.
     */
    public void markAsVerified() {
        this.verified = true;
    }

    /**
     * Adds a helpful vote to the review.
     */
    public void addHelpfulVote() {
        this.helpfulVotes++;
    }

    /**
     * Updates the review status.
     *
     * @param newStatus New status to set
     */
    public void updateStatus(ReviewStatus newStatus) {
        this.status = newStatus;
    }

    private void validateComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (comment.length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
    }

    // Entity equality based on identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductReview)) return false;
        ProductReview that = (ProductReview) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Rating getRating() {
        return rating;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean isVerified() {
        return verified;
    }

    public int getHelpfulVotes() {
        return helpfulVotes;
    }

    public ReviewStatus getStatus() {
        return status;
    }
}
