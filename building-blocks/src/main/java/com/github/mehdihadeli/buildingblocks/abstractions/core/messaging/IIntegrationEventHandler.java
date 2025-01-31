package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

public interface IIntegrationEventHandler<TMessage extends IIntegrationEvent> extends IMessageHandler<TMessage> {}
