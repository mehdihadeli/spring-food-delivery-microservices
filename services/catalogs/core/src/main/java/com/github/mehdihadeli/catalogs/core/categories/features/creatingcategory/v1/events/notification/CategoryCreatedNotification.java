package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1.events.notification;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEventHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandler;
import com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1.events.domain.CategoryCreated;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record CategoryCreatedNotification(CategoryCreated domainEvent, UUID notificationId)
        implements IDomainNotificationEvent<CategoryCreated> {}

@NotificationHandler
class CategoryCreatedNotificationHandler implements IDomainNotificationEventHandler<CategoryCreatedNotification> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryCreatedNotificationHandler.class);

    private final ExternalEventBus externalEventBus;

    public CategoryCreatedNotificationHandler(ExternalEventBus externalEventBus) {
        this.externalEventBus = externalEventBus;
    }

    @Override
    public void handle(CategoryCreatedNotification notification) throws RuntimeException {
        logger.atInfo()
                .addKeyValue("notification", notification)
                .log("notification event {} handled.", notification.getClass().getSimpleName());
        // We can publish integration event to bus here, but we used our eventmapper for mapping events and publishing
        // integration event
    }
}
