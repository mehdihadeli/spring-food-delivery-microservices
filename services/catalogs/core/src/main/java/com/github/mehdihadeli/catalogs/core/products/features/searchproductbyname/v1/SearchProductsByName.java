package com.github.mehdihadeli.catalogs.core.products.features.searchproductbyname.v1;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNullOrEmpty;

import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.buildingblocks.validation.Validator;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

public record SearchProductsByName(PageRequest pageRequest, String searchTerm)
        implements IQuery<SearchProductByNameResult> {
    public SearchProductsByName {
        notBeNull(pageRequest, "pageRequest");
        notBeNullOrEmpty(searchTerm, "searchTerm");
    }
}

@Component
class SearchProductsByNameValidator extends Validator<SearchProductsByName> {
    public SearchProductsByNameValidator() {
        stringRuleFor(SearchProductsByName::searchTerm, "searchTerm").notEmpty();
    }
}

@QueryHandler
class SearchProductsByNameHandler implements IQueryHandler<SearchProductsByName, SearchProductByNameResult> {

    private final ProductAggregateRepository productAggregateRepository;

    @PersistenceContext
    private EntityManager em;

    public SearchProductsByNameHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public SearchProductByNameResult handle(SearchProductsByName query) throws RuntimeException {
        var pageResult = this.productAggregateRepository.searchByName(
                org.springframework.data.domain.PageRequest.of(
                                query.pageRequest().getPageNumber(),
                                query.pageRequest().getPageSize())
                        .withSort(Sort.by("name").ascending()),
                query.searchTerm());

        // var pageList = PageList.fromSpringPage(pageResult, ProductMapper::toProductInfoDto);

        // return new SearchProductByNameResult(pageList);

        return null;
    }
}

record SearchProductByNameResult(PageList<ProductInfoDto> Products) {}
