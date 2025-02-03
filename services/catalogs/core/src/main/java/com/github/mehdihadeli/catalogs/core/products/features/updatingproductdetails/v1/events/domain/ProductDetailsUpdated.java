package com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1.events.domain;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Size;
import org.springframework.lang.Nullable;

public record ProductDetailsUpdated(
        ProductId productId,
        Name newName,
        @Nullable Description newDescription,
        Price newPrice,
        Dimensions newDimensions,
        Size newSize)
        implements IDomainEvent {

    public ProductDetailsUpdated {
        // Validation for nullability
        notBeNull(productId, "productId");
        notBeNull(newName, "newName");
        notBeNull(newPrice, "newPrice");
        notBeNull(newDimensions, "newDimensions");
        notBeNull(newSize, "newSize");
    }
}
