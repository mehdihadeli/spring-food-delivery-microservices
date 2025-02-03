package com.github.mehdihadeli.catalogs.products.features.gettingproductbyid.v1;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.dtos.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class GetProductByIdEndpoint {

    private final Mediator mediator;

    public GetProductByIdEndpoint(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Get product by id", description = "Get product by id", operationId = "GetProductById")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(
            responseCode = "404",
            description = "NotFound",
            content = @Content(schema = @Schema(implementation = NotFoundException.class)))
    @GetMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responseentity.html
    ResponseEntity<GetProductByIdResponse> getById(@PathVariable UUID id) {
        try (MDC.MDCCloseable md = MDC.putCloseable("productId", id.toString())) {
            var result = mediator.send(new GetProductById(new ProductId(id)));
            var response = new GetProductByIdResponse(result.Product());

            // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
            // `ResponseEntity` is like `@ResponseBody` but with `status` and headers.
            return ResponseEntity.ok(response);
        }
    }
}

record GetProductByIdResponse(ProductDto Product) {}
