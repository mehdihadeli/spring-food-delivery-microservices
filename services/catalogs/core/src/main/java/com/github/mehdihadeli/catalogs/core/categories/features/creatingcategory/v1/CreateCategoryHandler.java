package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.catalogs.core.categories.CategoryMapper;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandHandler
public class CreateCategoryHandler implements ICommandHandler<CreateCategory, CreateCategoryResult> {
    private final CategoryAggregateRepository categoryAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateCategoryHandler.class);

    public CreateCategoryHandler(CategoryAggregateRepository categoryAggregateRepository) {
        this.categoryAggregateRepository = categoryAggregateRepository;
    }

    @Override
    public CreateCategoryResult handle(CreateCategory command) throws RuntimeException {
        ValidationUtils.notBeNull(command, "command");

        // create category aggregate
        var category = CategoryMapper.toCategoryAggregate(command);

        var createdCategory = categoryAggregateRepository.add(category);

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("category", category)
                .log(
                        "category with id {} created successfully.",
                        category.getId().id());

        return new CreateCategoryResult(createdCategory.getId().id());
    }
}
