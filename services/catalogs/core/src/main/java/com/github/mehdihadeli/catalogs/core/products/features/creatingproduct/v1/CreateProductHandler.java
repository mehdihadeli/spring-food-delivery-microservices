package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandHandler
public class CreateProductHandler implements ICommandHandler<CreateProduct, CreateProductResult> {
    private final ProductAggregateRepository productAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateProductHandler.class);

    public CreateProductHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public CreateProductResult handle(CreateProduct command) {
        notBeNull(command, "command");

        // create product aggregate
        var product = ProductMapper.toProductAggregate(command);

        var createdProduct = productAggregateRepository.add(product);

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("product", product)
                .addKeyValue("productId", product.getId().id())
                .log(
                        "product {} with id {} created successfully.",
                        product.getClass().getSimpleName(),
                        product.getId().id());

        return new CreateProductResult(createdProduct.getId().id());
    }
}
