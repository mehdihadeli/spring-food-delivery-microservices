package com.github.mehdihadeli.catalogs.core.products.features.searchproductbyname.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/products/search-by-name")
public class SearchProductsByNameEndpoint {

    private final QueryBus queryBus;

    public SearchProductsByNameEndpoint(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_CATALOGS.READ') or hasAnyAuthority('CLAIM_CATALOGS.READ') or  hasAnyRole('CATALOGS:READ','ADMIN', 'CUSTOMER')")
    @Operation(
            summary = "Search products by name",
            description = "Search products by name",
            operationId = "SearchProductsByName")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<SearchProductsByNameResponse> searchProductsByNameResponse(
            @RequestParam String searchTerm, @RequestParam int pageNumber, @RequestParam int pageSize) {
        var result = queryBus.send(new SearchProductsByName(new PageRequest(pageNumber, pageSize), searchTerm));

        // https: // docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        return ResponseEntity.ok(new SearchProductsByNameResponse(result.Products()));
    }
}

// for showing response correctly in swagger we should not use IPageList
record SearchProductsByNameResponse(PageList<ProductInfoDto> Products) {}
