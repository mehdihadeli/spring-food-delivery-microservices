package com.github.mehdihadeli.catalogs.products.data.entities;

import com.github.mehdihadeli.catalogs.products.domain.models.entities.ReviewStatus;
import com.github.mehdihadeli.buildingblocks.core.data.AuditableEntityDataModel;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "product_reviews")
public class ProductReviewDataModel extends AuditableEntityDataModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductDataModel product;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private int helpfulVotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    // Default constructor for JPA
    public ProductReviewDataModel() {}

    public ProductDataModel getProduct() {
        return product;
    }

    public void setProduct(ProductDataModel product) {
        this.product = product;
    }

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
}
