package com.github.mehdihadeli.catalogs.core.products.features.deletingproduct;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommandUnit;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;

public record DeleteProduct(ProductId productId) implements ITxCommandUnit {}
