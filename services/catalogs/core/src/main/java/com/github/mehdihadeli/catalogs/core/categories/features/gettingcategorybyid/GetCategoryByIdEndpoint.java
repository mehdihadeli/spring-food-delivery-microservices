package com.github.mehdihadeli.catalogs.core.categories.features.gettingcategorybyid;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/categories")
public class GetCategoryByIdEndpoint {

    private final QueryBus queryBus;

    public GetCategoryByIdEndpoint(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Operation(summary = "Get category by id", description = "Get category by id", operationId = "GetCategoryById")
    @Tag(name = "Categories")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(
            responseCode = "404",
            description = "NotFound",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("{id}")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responseentity.html
    ResponseEntity<GetCategoryByIdResponse> getById(@PathVariable UUID id) {
        try (MDC.MDCCloseable md = MDC.putCloseable("categoryId", id.toString())) {
            var result = queryBus.send(new GetCategoryById(new CategoryId(id)));
            var response = new GetCategoryByIdResponse(result.category());

            // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
            // `ResponseEntity` is like `@ResponseBody` but with `status` and headers.
            return ResponseEntity.ok(response);
        }
    }
}
