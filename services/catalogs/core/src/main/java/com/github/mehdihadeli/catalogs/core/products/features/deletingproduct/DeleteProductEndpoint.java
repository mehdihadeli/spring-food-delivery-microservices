package com.github.mehdihadeli.catalogs.core.products.features.deletingproduct;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class DeleteProductEndpoint {

    private final CommandBus commandBus;

    public DeleteProductEndpoint(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_CATALOGS.WRITE') or hasAnyAuthority('CLAIM_CATALOGS.WRITE') or  hasAnyRole('CATALOGS:WRITE','ADMIN', 'CUSTOMER')")
    @Operation(summary = "Delete product", description = "Delete product", operationId = "DeleteProduct")
    @Tag(name = "Products")
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("productId", id.toString())) {
            var deleteProduct = new DeleteProduct(new ProductId(id));

            commandBus.send(deleteProduct);

            return ResponseEntity.noContent().build();
        }
    }
}
