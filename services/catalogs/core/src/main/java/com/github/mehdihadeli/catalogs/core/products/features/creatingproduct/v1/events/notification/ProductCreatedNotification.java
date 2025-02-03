package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.notification;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEventHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandler;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain.ProductCreated;
import java.util.UUID;

public record ProductCreatedNotification(ProductCreated domainEvent, UUID notificationId)
        implements IDomainNotificationEvent<ProductCreated> {}

@NotificationHandler
class ProductCreatedNotificationHandler implements IDomainNotificationEventHandler<ProductCreatedNotification> {

    private final ExternalEventBus externalEventBus;

    public ProductCreatedNotificationHandler(ExternalEventBus externalEventBus) {
        this.externalEventBus = externalEventBus;
    }

    @Override
    public void handle(ProductCreatedNotification notification) throws RuntimeException {
        System.out.println(notification);
        // We can publish integration event to bus here

        // var messageId = MessageUtils.generateMessageId();
        //        externalEventBus.publish(
        //                new
        // com.mehdihadeli.verticalslicetemplate.products.features.creatingproduct.v1.events.integration
        //                        .ProductCreated(
        //                        messageId,
        //                        notification.domainEvent().productId().id(),
        //                        notification.domainEvent().categoryId().id(),
        //                        notification.domainEvent().name().value(),
        //                        LocalDateTime.now()));
    }
}
