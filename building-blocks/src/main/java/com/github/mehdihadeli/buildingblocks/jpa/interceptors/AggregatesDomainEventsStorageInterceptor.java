package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IAggregateBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.AggregatesDomainEventsRequestStorage;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class AggregatesDomainEventsStorageInterceptor {
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        AggregatesDomainEventsStorageInterceptor.applicationContext = applicationContext;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void onEntityChange(Object entity) {
        if (entity instanceof IAggregateBase aggregateBase) {
            var aggregatesDomainEventsRequestStorage =
                    applicationContext.getBean(AggregatesDomainEventsRequestStorage.class);

            aggregatesDomainEventsRequestStorage.addEventsFromAggregate(aggregateBase);
        }
    }
}