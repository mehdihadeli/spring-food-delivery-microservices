package com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class UpdateProductDetailsEndpoint {

    private final CommandBus commandBus;

    public UpdateProductDetailsEndpoint(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PreAuthorize("hasAuthority('catalogs-update-claim') or hasAnyRole('admin', 'application')")
    @Operation(
            summary = "Update product details",
            description = "Update product details",
            operationId = "UpdateProductDetails")
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
    @PutMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<Void> updateProduct(@PathVariable UUID id, @RequestBody UpdateProductDetailsRequest request) {

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("productId", id.toString())) {
            var updateProductDetails = new UpdateProductDetails(
                    new ProductId(id),
                    new Name(request.name()),
                    new Price(request.priceAmount(), request.currency()),
                    new Dimensions(request.width(), request.height(), request.depth()),
                    new Size(request.size(), request.sizeUnit()),
                    new Description(request.description()));

            commandBus.send(updateProductDetails);

            return ResponseEntity.noContent().build();
        }
    }
}
