package com.github.mehdihadeli.buildingblocks.rabbitmq;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = CoreAutoConfiguration.class)
@Import(RabbitMQConfiguration.class)
@ConditionalOnClass({RabbitTemplate.class})
@EnableConfigurationProperties(CustomRabbitMQProperties.class)
// @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQAutoConfiguration {}
