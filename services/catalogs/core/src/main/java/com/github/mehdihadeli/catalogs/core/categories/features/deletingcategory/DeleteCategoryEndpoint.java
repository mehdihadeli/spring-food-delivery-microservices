package com.github.mehdihadeli.catalogs.core.categories.features.deletingcategory;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/categories")
public class DeleteCategoryEndpoint {

    private final CommandBus commandBus;

    public DeleteCategoryEndpoint(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Operation(summary = "Delete category", description = "Delete category", operationId = "DeleteCategory")
    @Tag(name = "Categories")
    @ApiResponse(
            responseCode = "404",
            description = "NotFound",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("categoryId", id.toString())) {
            var deleteCategory = new DeleteCategory(new CategoryId(id));

            commandBus.send(deleteCategory);

            return ResponseEntity.noContent().build();
        }
    }
}
