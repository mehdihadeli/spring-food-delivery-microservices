package com.github.mehdihadeli.buildingblocks.rabbitmq.test;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.TestHarness;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.buildingblocks.core.serialization.JacksonMessageSerializerImpl;
import com.github.mehdihadeli.buildingblocks.rabbitmq.PrePublishPipeline;
import org.springframework.amqp.core.Message;

public class TestPrePublishPipeline implements PrePublishPipeline {
    private final TestHarness harness;

    public TestPrePublishPipeline(TestHarness harness) {
        this.harness = harness;
    }

    @Override
    public Message handle(Message message) {
        var eventEnvelope = MessageUtils.convertMessageToEventEnvelope(message, new JacksonMessageSerializerImpl());
        harness.AddPublishedMessage(eventEnvelope);
        return message;
    }
}
