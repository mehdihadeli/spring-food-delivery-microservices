package com.github.mehdihadeli.catalogs.core.categories.features.deletingcategory;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnitHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandHandler
public class DeleteCategoryHandler implements ICommandUnitHandler<DeleteCategory> {
    private final CategoryAggregateRepository categoryAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeleteCategoryHandler.class);

    public DeleteCategoryHandler(CategoryAggregateRepository categoryAggregateRepository) {
        this.categoryAggregateRepository = categoryAggregateRepository;
    }

    @Override
    public Unit handle(DeleteCategory command) throws RuntimeException {
        notBeNull(command, "command");

        var category =
                categoryAggregateRepository.findById(command.categoryId()).orElse(null);
        if (category == null) {
            throw new NotFoundException(String.format(
                    "Category with id %s not found", command.categoryId().id()));
        }

        categoryAggregateRepository.delete(category);

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("category", category)
                .log("category with id {} deleted.", category.getId().id());

        return Unit.VALUE;
    }
}
