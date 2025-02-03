package com.github.mehdihadeli.catalogs.products.features.creatingproduct.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.catalogs.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.products.ProductMapper;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class CreateProductEndpoint {
    private final Mediator mediator;
    private final IdGenerator idGenerator;

    public CreateProductEndpoint(Mediator mediator, IdGenerator idGenerator) {
        this.mediator = mediator;
        this.idGenerator = idGenerator;
    }

    @Operation(summary = "Create product", description = "Create product", operationId = "CreateProduct")
    @Tag(name = "Products")
    @ApiResponse(responseCode = "201", description = "Created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<CreateProductResponse> createProduct(@RequestBody CreateProductRequest request)
            throws URISyntaxException {
        var productId = this.idGenerator.generateId();

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("productId", productId.toString())) {
            var productVariants = ProductMapper.toProductVariants(request.productVariants(), idGenerator);

            var createProduct = new CreateProduct(
                    new ProductId(productId),
                    new CategoryId(request.categoryId()),
                    new Name(request.name()),
                    new Price(request.priceAmount(), request.currency()),
                    request.status(),
                    new Dimensions(request.width(), request.height(), request.depth()),
                    new Size(request.size(), request.sizeUnit()),
                    productVariants,
                    new Description(request.description()));
            var result = mediator.send(createProduct);
            URI location = new URI(String.format("/api/v1/products/%s", result.Id()));

            return ResponseEntity.created(location).body(new CreateProductResponse(result.Id()));
        }
    }
}

record CreateProductResponse(UUID id) {}
