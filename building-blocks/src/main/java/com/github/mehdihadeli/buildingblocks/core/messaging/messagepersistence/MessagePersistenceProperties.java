package com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistProviderType;
import com.github.mehdihadeli.buildingblocks.jpamessagepersistence.JpaMessagePersistenceConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "message-persistence")
@ConditionalOnBean({JpaMessagePersistenceConfiguration.class})
public class MessagePersistenceProperties {
    private MessagePersistProviderType messagePersistProviderType = MessagePersistProviderType.InMemory;
    private Integer interval;

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public MessagePersistProviderType getMessagePersistProviderType() {
        return messagePersistProviderType;
    }

    public void setMessagePersistProviderType(MessagePersistProviderType messagePersistProviderType) {
        this.messagePersistProviderType = messagePersistProviderType;
    }
}
