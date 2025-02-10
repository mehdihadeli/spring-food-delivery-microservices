package com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommandUnit;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Size;
import org.springframework.lang.Nullable;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record UpdateProductDetails(
        ProductId productId,
        Name newName,
        Price newPrice,
        Dimensions newDimensions,
        Size newSize,
        @Nullable Description newDescription)
        implements ITxCommandUnit {
    public UpdateProductDetails {
        notBeNull(productId, "productId");
        notBeNull(newName, "newName");
        notBeNull(newPrice, "newPrice");
        notBeNull(newDimensions, "newDimensions");
        notBeNull(newSize, "newSize");
    }
}
