package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.notification;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEventHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandler;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain.ProductCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public record ProductCreatedNotification(ProductCreated domainEvent, UUID notificationId)
        implements IDomainNotificationEvent<ProductCreated> {}

@NotificationHandler
class ProductCreatedNotificationHandler implements IDomainNotificationEventHandler<ProductCreatedNotification> {
    private static final Logger logger = LoggerFactory.getLogger(ProductCreatedNotificationHandler.class);
    private final ExternalEventBus externalEventBus;

    public ProductCreatedNotificationHandler(ExternalEventBus externalEventBus) {
        this.externalEventBus = externalEventBus;
    }

    @Override
    public void handle(ProductCreatedNotification notification) throws RuntimeException {
        logger.atInfo()
                .addKeyValue("notification", notification)
                .log("notification event {} handled.", notification.getClass().getSimpleName());
        // We can publish integration event to bus here, but we used our eventmapper for mapping events and publishing
        // integration event
    }
}
