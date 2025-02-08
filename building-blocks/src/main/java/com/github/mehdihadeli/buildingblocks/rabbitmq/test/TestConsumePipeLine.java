package com.github.mehdihadeli.buildingblocks.rabbitmq.test;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.TestHarness;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.buildingblocks.core.serialization.JacksonMessageSerializerImpl;
import com.github.mehdihadeli.buildingblocks.rabbitmq.ConsumePipeline;
import org.springframework.amqp.core.Message;

public class TestConsumePipeLine implements ConsumePipeline {
    private final TestHarness harness;

    public TestConsumePipeLine(TestHarness harness) {
        this.harness = harness;
    }

    @Override
    public Message handle(Message message) {
        var eventEnvelope = MessageUtils.convertMessageToEventEnvelope(message, new JacksonMessageSerializerImpl());
        harness.AddConsumeMessage(eventEnvelope);

        return message;
    }
}
