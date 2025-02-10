package com.github.mehdihadeli.catalogs.core.products.features.deletingproduct;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.core.exceptions.NotFoundException;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnitHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandHandler
public class DeleteProductHandler implements ICommandUnitHandler<DeleteProduct> {
    private final ProductAggregateRepository productAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeleteProductHandler.class);

    public DeleteProductHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public Unit handle(DeleteProduct command) throws RuntimeException {
        notBeNull(command, "command");

        var product = productAggregateRepository.findById(command.productId()).orElse(null);
        if (product == null) {
            throw new NotFoundException(String.format(
                    "Product with id %s not found", command.productId().id()));
        }

        productAggregateRepository.delete(product);

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("product", product)
                .log("product with id {} deleted.", product.getId().id());

        return Unit.VALUE;
    }
}
