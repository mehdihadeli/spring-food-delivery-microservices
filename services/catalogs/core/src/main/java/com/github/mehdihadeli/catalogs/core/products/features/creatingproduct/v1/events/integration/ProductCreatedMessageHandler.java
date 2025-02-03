package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.integration;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessageHandler;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.MessageHandler;

@MessageHandler
public class ProductCreatedMessageHandler implements IMessageHandler<ProductCreated> {
    @Override
    public void Handle(IEventEnvelope<ProductCreated> eventEnvelope) {

        System.out.println(eventEnvelope);
    }
}
