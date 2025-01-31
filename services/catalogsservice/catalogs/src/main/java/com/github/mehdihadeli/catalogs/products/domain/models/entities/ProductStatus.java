package com.github.mehdihadeli.catalogs.products.domain.models.entities;

public enum ProductStatus {
    Active,
    Inactive;

    public boolean canTransitionTo(ProductStatus newStatus) {
        if (this == newStatus) {
            return true; // Always allow transition to same status
        }

        return switch (this) {
            case Active -> newStatus == Inactive;
            case Inactive -> newStatus == Active;
            default -> false;
        };
    }
}
