package com.github.mehdihadeli.catalogs.core.products;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.EventMapper;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.Mapper;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain.ProductCreated;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.notification.ProductCreatedNotification;
import java.time.LocalDateTime;

@Mapper
public class ProductEventMapper implements EventMapper {

    @Override
    public IIntegrationEvent mapToIntegrationEvent(IDomainEvent domainEvent) {
        return switch (domainEvent) {
                // Materialize domain event to integration event
            case ProductCreated productCreated -> new com.github.mehdihadeli.catalogs.core.products.features
                    .creatingproduct.v1.events.integration.ProductCreated(
                    MessageUtils.generateMessageId(),
                    productCreated.productId().id(),
                    productCreated.categoryId().id(),
                    productCreated.name().value(),
                    LocalDateTime.now());
            default -> null;
        };
    }

    @Override
    public <T extends IDomainEvent> IDomainNotificationEvent<T> mapToDomainNotificationEvent(T domainEvent) {
        return switch (domainEvent) {
                // Materialize domain event to integration event
            case ProductCreated productCreated -> (IDomainNotificationEvent<T>)
                    new ProductCreatedNotification(productCreated, MessageUtils.generateNotificationId());
            default -> null;
        };
    }
}
