package com.github.mehdihadeli.buildingblocks.core.messaging;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.TestHarness;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestHarnessImpl implements TestHarness {
    private final List<IMessageEnvelopeBase> _publishedMessages = new ArrayList<>();
    private final List<IMessageEnvelopeBase> _consumedMessages = new ArrayList<>();

    @Override
    public void AddConsumeMessage(IMessageEnvelopeBase messageEnvelopeBase) {
        _consumedMessages.add(messageEnvelopeBase);
    }

    @Override
    public void AddPublishedMessage(IMessageEnvelopeBase messageEnvelopeBase) {
        _publishedMessages.add(messageEnvelopeBase);
    }

    @Override
    public <T extends IMessage> void waitForPublishedMessage(Class<T> messageClass) {
        waitUntilConditionMet(() -> isPublishedMessage(messageClass));
    }

    @Override
    public <T extends IMessage> void waitForConsumedMessage(Class<T> messageClass) {
        waitUntilConditionMet(() -> isConsumedMessage(messageClass));
    }

    @Override
    public <T extends IMessage> boolean isConsumedMessage(Class<T> messageClass) {
        var message = _consumedMessages.stream()
                .filter(m -> m.message().getClass().equals(messageClass))
                .findFirst()
                .orElse(null);

        return message != null;
    }

    @Override
    public <T extends IMessage> boolean isPublishedMessage(Class<T> messageClass) {
        var message = _publishedMessages.stream()
                .filter(m -> m.message().getClass().equals(messageClass))
                .findFirst()
                .orElse(null);

        return message != null;
    }

    public void waitUntilConditionMet(Callable<Boolean> conditionToMet) {
        int timeout = 300; // Default to 300 seconds
        long startTime = System.currentTimeMillis();
        boolean conditionMet;
        try {
            conditionMet = conditionToMet.call();

            while (!conditionMet) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                if (elapsedTime > timeout) {
                    try {
                        throw new TimeoutException("Condition not met within the timeout period.");
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }

                TimeUnit.MILLISECONDS.sleep(100);
                conditionMet = conditionToMet.call();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
