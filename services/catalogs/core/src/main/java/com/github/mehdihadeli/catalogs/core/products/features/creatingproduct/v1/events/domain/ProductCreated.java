package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEventHandler;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandler;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductReview;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductVariant;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Size;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.internal.mongo.CreateProductRead;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;

// https://event-driven.io/en/explicit_validation_in_csharp_just_got_simpler/
// https://event-driven.io/en/how_to_validate_business_logic/
// https://event-driven.io/en/notes_about_csharp_records_and_nullable_reference_types/
// https://buildplease.com/pages/vos-in-events/
// https://codeopinion.com/leaking-value-objects-from-your-domain/
// https://www.youtube.com/watch?v=CdanF8PWJng
// if we want to store domain events in eventstoredb we should use primitive types instead of value obejcts and entities
// because with changing in rules and filed in vo our old events can't restore successfully.
public record ProductCreated(
        ProductId productId,
        CategoryId categoryId,
        Name name,
        Price price,
        ProductStatus status,
        Dimensions dimensions,
        Size size,
        @Nullable List<ProductReview> reviews,
        @Nullable Set<ProductVariant> variants,
        @Nullable Description description)
        implements IDomainEvent {

    public ProductCreated {
        notBeNull(productId, "productId");
        notBeNull(categoryId, "categoryId");
        notBeNull(name, "name");
        notBeNull(price, "price");
        notBeNull(status, "status");
        notBeNull(dimensions, "dimensions");
        notBeNull(size, "size");
    }
}

@NotificationHandler
class ProductCreatedDomainHandler implements IDomainEventHandler<ProductCreated> {

    private final CommandBus commandBus;

    ProductCreatedDomainHandler(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(ProductCreated productCreated) throws RuntimeException {
        var productReadModel = ProductMapper.toProductReadModel(productCreated);
        // https://github.com/kgrzybek/modular-monolith-with-ddd#38-internal-processing
        // Schedule multiple read sides to execute here
        commandBus.schedule(new CreateProductRead(MessageUtils.generateInternalId(), productReadModel));
    }
}
