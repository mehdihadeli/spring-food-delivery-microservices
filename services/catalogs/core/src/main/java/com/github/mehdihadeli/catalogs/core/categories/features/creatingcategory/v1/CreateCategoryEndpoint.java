package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/categories")
public class CreateCategoryEndpoint {
    private final CommandBus commandBus;
    private final IdGenerator idGenerator;

    public CreateCategoryEndpoint(CommandBus commandBus, IdGenerator idGenerator) {
        this.commandBus = commandBus;
        this.idGenerator = idGenerator;
    }

    @Operation(summary = "Create Category", description = "Create Category", operationId = "CreateCategory")
    @Tag(name = "Categories")
    @ApiResponse(responseCode = "201", description = "Created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<CreateCategoryResponse> createCategory(@RequestBody CreateCategoryRequest request)
            throws URISyntaxException {
        var categoryId = this.idGenerator.generateId();

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("categoryId", categoryId.toString())) {

            var createCategory = new CreateCategory(
                    new CategoryId(categoryId),
                    new Name(request.name()),
                    new Code(request.code()),
                    new Description(request.description()));
            var result = commandBus.send(createCategory);
            URI location = new URI(String.format("/api/v1/categories/%s", result.Id()));

            return ResponseEntity.created(location).body(new CreateCategoryResponse(result.Id()));
        }
    }
}
