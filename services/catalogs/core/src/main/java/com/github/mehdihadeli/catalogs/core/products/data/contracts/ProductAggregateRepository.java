package com.github.mehdihadeli.catalogs.core.products.data.contracts;

import com.github.mehdihadeli.catalogs.core.products.data.projections.ProductSummaryProjection;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.Product;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductSummaryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductAggregateRepository {
    Optional<Product> findById(ProductId productId);

    Product add(Product product);

    void update(Product product);

    void delete(Product product);

    Page<ProductSummaryProjection> findByPage(Pageable pageable);

    Page<ProductSummaryDTO> searchByName(Pageable pageable, String name);
}
