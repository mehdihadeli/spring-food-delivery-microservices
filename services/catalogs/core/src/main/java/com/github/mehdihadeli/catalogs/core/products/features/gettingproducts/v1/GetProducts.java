package com.github.mehdihadeli.catalogs.core.products.features.gettingproducts.v1;

import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;

public record GetProducts(PageRequest pageRequest) implements IQuery<GetProductsResult> {
    public GetProducts {
        ValidationUtils.notBeNull(pageRequest, "pageRequest");
    }
}

@QueryHandler
class GetProductsHandler implements IQueryHandler<GetProducts, GetProductsResult> {

    private final ProductAggregateRepository productAggregateRepository;

    @PersistenceContext
    private EntityManager em;

    public GetProductsHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public GetProductsResult handle(GetProducts query) {
        var pageResult = this.productAggregateRepository.findByPage(org.springframework.data.domain.PageRequest.of(
                        query.pageRequest().getPageNumber(), query.pageRequest().getPageSize())
                .withSort(Sort.by("name").ascending()));

        var pageList = PageList.fromSpringPage(pageResult, ProductMapper::toProductInfoDto);

        return new GetProductsResult(pageList);
    }
}

record GetProductsResult(PageList<ProductInfoDto> products) {}
