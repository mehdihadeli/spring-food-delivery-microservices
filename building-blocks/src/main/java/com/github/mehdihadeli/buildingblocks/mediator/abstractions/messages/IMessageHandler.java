package com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages;

public interface IMessageHandler<TMessage extends IMessage> {
    void handle(TMessage message);
}
