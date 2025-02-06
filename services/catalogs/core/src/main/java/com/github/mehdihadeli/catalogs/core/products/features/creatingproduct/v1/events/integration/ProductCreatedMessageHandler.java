package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.integration;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.MessageHandler;

@MessageHandler
public class ProductCreatedMessageHandler implements IMessageEnvelopeHandler<ProductCreated> {
    @Override
    public void handle(IMessageEnvelope<ProductCreated> eventEnvelope) {

        System.out.println(eventEnvelope);
    }
}
