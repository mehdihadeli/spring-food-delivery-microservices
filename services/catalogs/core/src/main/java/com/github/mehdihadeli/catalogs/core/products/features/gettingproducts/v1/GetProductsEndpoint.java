package com.github.mehdihadeli.catalogs.core.products.features.gettingproducts.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class GetProductsEndpoint {

    private final QueryBus queryBus;

    public GetProductsEndpoint(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_CATALOGS.READ') or hasAnyAuthority('CLAIM_CATALOGS.READ') or hasAnyRole('CATALOGS:READ') or hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "Get products by page", description = "Get products by page", operationId = "GetProducts")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(
            responseCode = "401",
            description = "UnAuthorize",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ResponseStatus(HttpStatus.OK)
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    @ResponseBody
    @GetMapping
    GetProductsByPageResponse getProducts(@RequestParam int pageNumber, @RequestParam int pageSize) {
        var result = queryBus.send(new GetProducts(new PageRequest(pageNumber, pageSize)));

        // https: // docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        return new GetProductsByPageResponse(result.products());
    }
}

// for showing response correctly in swagger we should not use IPageList
record GetProductsByPageResponse(PageList<ProductInfoDto> products) {}
