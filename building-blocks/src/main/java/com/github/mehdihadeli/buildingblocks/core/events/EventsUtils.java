package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IHaveExternalEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IHaveNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.Nullable;

public class EventsUtils {
    public static <T extends IDomainEvent> @Nullable IDomainNotificationEvent<T> getWrappedDomainNotificationEvent(
            IDomainEvent domainEvent) {
        if (IHaveNotificationEvent.class.isAssignableFrom(domainEvent.getClass())) {
            try {
                // Get the wrapper class
                Class<?> wrapperClass = DomainNotificationEventWrapper.class;

                // Get the constructor with the correct parameter types
                Constructor<?> constructor = wrapperClass.getConstructor(IDomainEvent.class, UUID.class);

                // Create a new instance of the wrapper
                return (IDomainNotificationEvent<T>)
                        constructor.newInstance(domainEvent, MessageUtils.generateNotificationId());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create domain notification event wrapper", e);
            }
        }

        return null;
    }

    public static @Nullable IIntegrationEvent getWrappedIntegrationEvent(IDomainEvent domainEvent) {
        if (IHaveExternalEvent.class.isAssignableFrom(domainEvent.getClass())) {
            try {
                // Get the wrapper class
                Class<?> wrapperClass = IntegrationEventWrapper.class;

                // Get the constructor with the correct parameter types
                Constructor<?> constructor = wrapperClass.getConstructor(
                        IDomainEvent.class, // domainEvent
                        UUID.class, // messageId
                        LocalDateTime.class // created
                        );

                // Create a new instance of the wrapper
                return (IIntegrationEvent) constructor.newInstance(
                        domainEvent, // domainEvent
                        MessageUtils.generateMessageId(), // messageId
                        LocalDateTime.now() // created
                        );
            } catch (Exception e) {
                throw new RuntimeException("Failed to create integration event wrapper", e);
            }
        }

        return null;
    }
}
