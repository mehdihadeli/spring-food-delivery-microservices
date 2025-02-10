package com.github.mehdihadeli.catalogs.core.products.features.updatingproductdetails.v1;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnitHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

@CommandHandler
public class UpdateProductDetailsHandler implements ICommandUnitHandler<UpdateProductDetails> {
    private final ProductAggregateRepository productAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(UpdateProductDetailsHandler.class);

    public UpdateProductDetailsHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public Unit handle(UpdateProductDetails command) throws RuntimeException {
        notBeNull(command, "command");

        var product = productAggregateRepository.findById(command.productId()).orElse(null);
        if (product == null) {
            throw new NotFoundException(String.format(
                    "Product with id %s not found", command.productId().id()));
        }

        product.updateProductDetails(
                command.newName(),
                command.newPrice(),
                command.newDimensions(),
                command.newSize(),
                command.newDescription());

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("product", product)
                .log("product with id {} updated successfully.", product.getId().id());

        return Unit.VALUE;
    }
}
