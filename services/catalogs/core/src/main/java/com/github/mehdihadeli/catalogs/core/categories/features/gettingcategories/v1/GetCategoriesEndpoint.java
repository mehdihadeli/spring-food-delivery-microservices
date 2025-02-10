package com.github.mehdihadeli.catalogs.core.categories.features.gettingcategories.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.catalogs.core.categories.dtos.CategoryInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/categories")
public class GetCategoriesEndpoint {

    private final QueryBus queryBus;

    public GetCategoriesEndpoint(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Operation(
            summary = "Get categories by page",
            description = "Get categories by page",
            operationId = "GetCategories")
    @Tag(name = "Categories")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ResponseStatus(HttpStatus.OK)
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    @ResponseBody
    @GetMapping
    GetProductsByPageResponse getCategories(@RequestParam int pageNumber, @RequestParam int pageSize) {
        var result = queryBus.send(new GetCategories(new PageRequest(pageNumber, pageSize)));

        // https: // docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        return new GetProductsByPageResponse(result.categories());
    }
}

// for showing response correctly in swagger we should not use IPageList
record GetProductsByPageResponse(PageList<CategoryInfoDto> categories) {}
