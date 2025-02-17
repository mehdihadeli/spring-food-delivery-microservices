package com.github.mehdihadeli.customers.core.users.features.v1.events.integration.external;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.MessageHandler;
import com.github.mehdihadeli.shared.Constants;
import com.github.mehdihadeli.shared.users.users.events.integration.v1.UserCreatedV1;

@MessageHandler
public class UsersCreatedV1Consumer implements IMessageEnvelopeHandler<UserCreatedV1> {
    @Override
    public void handle(IMessageEnvelope<UserCreatedV1> eventEnvelope) {
        System.out.println(eventEnvelope);
        if (eventEnvelope.message().roles().contains(Constants.Roles.CUSTOMER)) {
            // create customer in customers service database
        }
    }
}
