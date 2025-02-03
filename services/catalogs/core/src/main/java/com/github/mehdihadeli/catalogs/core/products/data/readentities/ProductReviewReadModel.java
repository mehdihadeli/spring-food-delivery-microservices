package com.github.mehdihadeli.catalogs.core.products.data.readentities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableReadEntity;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ReviewStatus;
import java.util.UUID;

public class ProductReviewReadModel extends AuditableReadEntity {
    private UUID productId;
    private UUID reviewId;
    private UUID customerId;
    private Integer rating;
    private String comment;
    private boolean verified;
    private int helpfulVotes;
    private ReviewStatus status;

    // Constructor for mapping from ProductReviewDataModel
    public ProductReviewReadModel(
            UUID id,
            UUID productId,
            UUID customerId,
            Integer rating,
            String comment,
            boolean verified,
            int helpfulVotes,
            ReviewStatus status) {
        this.setId(id);
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.verified = verified;
        this.helpfulVotes = helpfulVotes;
        this.status = status;
    }

    // Private default constructor for jpa mongo and Jackson serializer
    private ProductReviewReadModel() {}

    // Getters and Setters
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getHelpfulVotes() {
        return helpfulVotes;
    }

    public void setHelpfulVotes(int helpfulVotes) {
        this.helpfulVotes = helpfulVotes;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
