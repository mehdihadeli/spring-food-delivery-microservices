package com.github.mehdihadeli.catalogs.core.products.features.gettingproducts.v1;

import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class GetProductsEndpoint {
    private final Mediator mediator;

    public GetProductsEndpoint(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Get products by page", description = "Get products by page", operationId = "GetProducts")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ResponseStatus(HttpStatus.OK)
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    @ResponseBody
    @GetMapping
    GetProductsByPageResponse getProducts(@RequestParam int pageNumber, @RequestParam int pageSize) {
        var result = mediator.send(new GetProducts(new PageRequest(pageNumber, pageSize)));

        // https: // docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        return new GetProductsByPageResponse(result.Products());
    }
}

// for showing response correctly in swagger we should not use IPageList
record GetProductsByPageResponse(PageList<ProductInfoDto> Products) {}
