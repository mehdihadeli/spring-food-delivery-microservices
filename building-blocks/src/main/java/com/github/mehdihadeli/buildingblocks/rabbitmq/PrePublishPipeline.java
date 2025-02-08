package com.github.mehdihadeli.buildingblocks.rabbitmq;

import org.springframework.amqp.core.Message;

public interface PrePublishPipeline {
    Message handle(Message message);
}
