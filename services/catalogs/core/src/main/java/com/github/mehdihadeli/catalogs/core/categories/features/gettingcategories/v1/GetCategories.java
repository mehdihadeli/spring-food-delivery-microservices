package com.github.mehdihadeli.catalogs.core.categories.features.gettingcategories.v1;

import com.github.mehdihadeli.buildingblocks.core.pagination.PageList;
import com.github.mehdihadeli.buildingblocks.core.pagination.PageRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.core.categories.CategoryMapper;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import com.github.mehdihadeli.catalogs.core.categories.dtos.CategoryInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;

public record GetCategories(PageRequest pageRequest) implements IQuery<GetCategoriesResult> {
    public GetCategories {
        ValidationUtils.notBeNull(pageRequest, "pageRequest");
    }
}

@QueryHandler
class GetCategoriesHandler implements IQueryHandler<GetCategories, GetCategoriesResult> {

  private final CategoryAggregateRepository categoryAggregateRepository;

  @PersistenceContext
  private EntityManager em;

  public GetCategoriesHandler(CategoryAggregateRepository categoryAggregateRepository) {
    this.categoryAggregateRepository = categoryAggregateRepository;
  }

  @Override
  public GetCategoriesResult handle(GetCategories query) {
    var pageResult = this.categoryAggregateRepository.findByPage(org.springframework.data.domain.PageRequest.of(
        query.pageRequest().getPageNumber(), query.pageRequest().getPageSize())
      .withSort(Sort.by("name").ascending()));

    var pageList = PageList.fromSpringPage(pageResult, CategoryMapper::toCategoryInfoDto);

    return new GetCategoriesResult(pageList);
  }
}

record GetCategoriesResult(PageList<CategoryInfoDto> categories){}

