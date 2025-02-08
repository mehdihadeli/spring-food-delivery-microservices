package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;

public interface TestHarness {
    void AddConsumeMessage(IMessageEnvelopeBase messageEnvelopeBase);

    void AddPublishedMessage(IMessageEnvelopeBase messageEnvelopeBase);

    <T extends IMessage> void waitForPublishedMessage(Class<T> messageClass);

    <T extends IMessage> void waitForConsumedMessage(Class<T> messageClass);

    <T extends IMessage> boolean isConsumedMessage(Class<T> messageClass);

    <T extends IMessage> boolean isPublishedMessage(Class<T> messageClass);
}
