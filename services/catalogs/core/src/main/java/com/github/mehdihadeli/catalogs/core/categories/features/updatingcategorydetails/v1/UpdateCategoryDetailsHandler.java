package com.github.mehdihadeli.catalogs.core.categories.features.updatingcategorydetails.v1;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnitHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.categories.data.contracts.CategoryAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

@CommandHandler
public class UpdateCategoryDetailsHandler implements ICommandUnitHandler<UpdateCategoryDetails> {
    private final CategoryAggregateRepository categoryAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(UpdateCategoryDetailsHandler.class);

    public UpdateCategoryDetailsHandler(CategoryAggregateRepository categoryAggregateRepository) {
        this.categoryAggregateRepository = categoryAggregateRepository;
    }

    @Override
    public Unit handle(UpdateCategoryDetails command) throws RuntimeException {
        notBeNull(command, "command");

        var category =
                categoryAggregateRepository.findById(command.categoryId()).orElse(null);
        if (category == null) {
            throw new NotFoundException(String.format(
                    "category with id %s not found", command.categoryId().id()));
        }

        category.updateCategoryDetails(command.newName(), command.newCode(), command.newDescription());

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("category", category)
                .log(
                        "category with id {} updated successfully.",
                        category.getId().id());

        return Unit.VALUE;
    }
}
