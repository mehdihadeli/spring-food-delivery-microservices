package com.github.mehdihadeli.buildingblocks.postgresmessagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean({MessagePersistenceService.class})
@ConditionalOnClass({MessagePersistenceService.class})
public class PostgresMessagePersistenceConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "message-persistence",
            name = "message-persist-provider-type",
            havingValue = "postgresql",
            matchIfMissing = false)
    MessagePersistenceRepository messagePersistenceRepository(EntityManager entityManager) {
        return new PostgresMessagePersistenceRepositoryImpl(entityManager);
    }
}