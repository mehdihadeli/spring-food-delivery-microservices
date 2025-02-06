package com.github.mehdihadeli.buildingblocks.jpamessagepersistence;

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
public class JpaMessagePersistenceConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "message-persistence",
            name = "message-persist-provider-type",
            havingValue = "postgresql",
            matchIfMissing = false)
    MessagePersistenceRepository messagePersistenceRepository(EntityManager entityManager) {
        return new JpaMessagePersistenceRepositoryImpl(entityManager);
    }
}
