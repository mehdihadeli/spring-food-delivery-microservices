package com.github.mehdihadeli.buildingblocks.rabbitmq;

import org.springframework.amqp.core.Message;

public interface ConsumePipeline {
    Message handle(Message message);
}
