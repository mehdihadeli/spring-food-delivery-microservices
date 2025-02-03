package com.github.mehdihadeli.catalogs.products.exceptions;

import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.SKU;

public class DuplicateSkuException extends DomainException {
    public DuplicateSkuException(SKU sku, ProductId productId) {
        super(String.format("Product %s already has variant with SKU %s", productId.id(), sku.value()));
    }
}
