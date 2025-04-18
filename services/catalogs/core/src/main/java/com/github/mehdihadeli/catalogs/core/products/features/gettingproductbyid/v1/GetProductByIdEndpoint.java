package com.github.mehdihadeli.catalogs.core.products.features.gettingproductbyid.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class GetProductByIdEndpoint {

    private final QueryBus queryBus;

    public GetProductByIdEndpoint(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_CATALOGS.READ') or hasAnyAuthority('CLAIM_CATALOGS.READ') or  hasAnyRole('CATALOGS:READ','ADMIN', 'CUSTOMER')")
    @Operation(summary = "Get product by id", description = "Get product by id", operationId = "GetProductById")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(
            responseCode = "404",
            description = "NotFound",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(
            responseCode = "401",
            description = "UnAuthorize",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responseentity.html
    ResponseEntity<GetProductByIdResponse> getById(@PathVariable UUID id) {
        try (MDC.MDCCloseable md = MDC.putCloseable("productId", id.toString())) {
            var result = queryBus.send(new GetProductById(new ProductId(id)));
            var response = new GetProductByIdResponse(result.product());

            // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
            // `ResponseEntity` is like `@ResponseBody` but with `status` and headers.
            return ResponseEntity.ok(response);
        }
    }
}

record GetProductByIdResponse(ProductDto product) {}
