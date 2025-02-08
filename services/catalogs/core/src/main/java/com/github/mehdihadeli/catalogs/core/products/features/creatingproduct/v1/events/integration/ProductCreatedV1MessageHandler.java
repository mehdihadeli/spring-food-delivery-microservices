package com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.integration;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.MessageHandler;
import com.github.mehdihadeli.shared.catalogs.products.events.integration.v1.ProductCreatedV1;

@MessageHandler
public class ProductCreatedV1MessageHandler implements IMessageEnvelopeHandler<ProductCreatedV1> {
    @Override
    public void handle(IMessageEnvelope<ProductCreatedV1> eventEnvelope) {

        System.out.println(eventEnvelope);
    }
}
