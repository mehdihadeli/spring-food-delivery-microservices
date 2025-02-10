package com.github.mehdihadeli.catalogs.core.categories.features.gettingcategorybyid;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.catalogs.core.categories.CategoryMapper;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import com.github.mehdihadeli.catalogs.core.categories.dtos.CategoryDto;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

@QueryHandler
public class GetCategoryByIdHandler implements IQueryHandler<GetCategoryById, GetCategoryByIdResult> {

    private final CategoryAggregateRepository categoryAggregateRepository;

    public GetCategoryByIdHandler(CategoryAggregateRepository categoryAggregateRepository) {
        this.categoryAggregateRepository = categoryAggregateRepository;
    }

    @Override
    public GetCategoryByIdResult handle(GetCategoryById query) {
        notBeNull(query, "query");

        var category = categoryAggregateRepository.findById(query.categoryId()).orElse(null);
        if (category == null) {
            throw new NotFoundException(String.format(
                    "Category with id %s not found", query.categoryId().id()));
        }

        // Map product data model to DTO
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);

        return new GetCategoryByIdResult(categoryDto);
    }
}
