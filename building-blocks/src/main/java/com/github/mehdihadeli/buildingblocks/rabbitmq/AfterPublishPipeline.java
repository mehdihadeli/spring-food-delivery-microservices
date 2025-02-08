package com.github.mehdihadeli.buildingblocks.rabbitmq;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public interface AfterPublishPipeline {
    void handle(CorrelationData correlationData, boolean ack);
}
