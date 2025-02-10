package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.internal.mongo;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductReadJpaRepository;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductReadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record CreateProductRead(UUID internalCommandId, ProductReadModel productReadModel) implements IInternalCommand {
    public CreateProductRead {
        notBeEmpty(internalCommandId, "internalCommandId");
        notBeNull(productReadModel, "productReadModel");
    }
}

@CommandHandler
class CreateProductReadHandler implements ICommandHandler<CreateProductRead, Unit> {
    private static final Logger logger = LoggerFactory.getLogger(CreateProductReadHandler.class);
    private final ProductReadJpaRepository productReadJpaRepository;

    public CreateProductReadHandler(ProductReadJpaRepository productReadJpaRepository) {
        this.productReadJpaRepository = productReadJpaRepository;
    }

    @Override
    public Unit handle(CreateProductRead command) throws RuntimeException {
        productReadJpaRepository.save(command.productReadModel());

        logger.atInfo()
                .addKeyValue("internal-command", command)
                .log("internal command {} handled.", command.getClass().getSimpleName());

        return Unit.VALUE;
    }
}
