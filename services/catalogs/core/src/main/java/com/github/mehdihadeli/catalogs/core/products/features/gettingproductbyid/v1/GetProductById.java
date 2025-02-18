package com.github.mehdihadeli.catalogs.core.products.features.gettingproductbyid.v1;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.buildingblocks.validation.Validator;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductDto;
import org.springframework.stereotype.Component;

public record GetProductById(ProductId productId) implements IQuery<GetProductByIdResult> {
    public GetProductById {
        notBeNull(productId, "productId");
    }
}

@Component
class GetProductByIdValidator extends Validator<GetProductById> {
    public GetProductByIdValidator() {
        objectRuleFor(GetProductById::productId, "productId").notNull();
    }
}

@QueryHandler
class GetProductByIdHandler implements IQueryHandler<GetProductById, GetProductByIdResult> {

    private final ProductAggregateRepository productAggregateRepository;

    public GetProductByIdHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public GetProductByIdResult handle(GetProductById query) {
        notBeNull(query, "query");

        var product = productAggregateRepository.findById(query.productId()).orElse(null);
        if (product == null) {
            throw new NotFoundException(String.format(
                    "Product with id %s not found", query.productId().id()));
        }

        // Map product data model to DTO
        ProductDto productDto = ProductMapper.toProductDto(product);

        return new GetProductByIdResult(productDto);
    }
}

record GetProductByIdResult(ProductDto product) {}
