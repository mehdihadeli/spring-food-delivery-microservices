package com.github.mehdihadeli.catalogs.core.categories.features.updatingcategorydetails.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("api/v1/categories")
public class UpdateCategoryDetailsEndpoint {

    private final CommandBus commandBus;

    public UpdateCategoryDetailsEndpoint(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Operation(
            summary = "Update category details",
            description = "Update category details",
            operationId = "UpdateCategoryDetails")
    @Tag(name = "Categories")
    @ApiResponse(
            responseCode = "404",
            description = "NotFound",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<Void> updateCategory(@PathVariable UUID id, @RequestBody UpdateCategoryDetailsRequest request) {

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("categoryId", id.toString())) {
            var updateCategoryDetails = new UpdateCategoryDetails(
                    new CategoryId(id),
                    new Name(request.name()),
                    new Code(request.code()),
                    new Description(request.description()));

            commandBus.send(updateCategoryDetails);

            return ResponseEntity.noContent().build();
        }
    }
}
