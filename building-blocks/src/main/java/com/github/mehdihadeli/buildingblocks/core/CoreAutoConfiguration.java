package com.github.mehdihadeli.buildingblocks.core;

import com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence.MessagePersistenceProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import({CoreConfiguration.class})
@EnableConfigurationProperties(MessagePersistenceProperties.class)
@AutoConfiguration
public class CoreAutoConfiguration {}
