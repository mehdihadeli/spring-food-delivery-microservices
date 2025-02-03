package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.internal.mongo;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductReadJpaRepository;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductReadModel;
import java.util.UUID;

public record CreateProductRead(UUID internalCommandId, ProductReadModel productReadModel) implements IInternalCommand {
    public CreateProductRead {
        notBeEmpty(internalCommandId, "internalCommandId");
        notBeNull(productReadModel, "productReadModel");
    }
}

@CommandHandler
class CreateProductReadHandler implements ICommandHandler<CreateProductRead, Unit> {
    private final ProductReadJpaRepository productReadJpaRepository;

    public CreateProductReadHandler(ProductReadJpaRepository productReadJpaRepository) {
        this.productReadJpaRepository = productReadJpaRepository;
    }

    @Override
    public Unit handle(CreateProductRead command) throws RuntimeException {
        productReadJpaRepository.save(command.productReadModel());

        return Unit.VALUE;
    }
}
