package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.events.IEvent;
import java.util.List;

public interface InternalEventBus {
    /**
     * Publish an in-memory event.
     *
     * @param eventData the event to be published
     * @throws RuntimeException if the operation is interrupted
     */
    void publish(IEvent eventData) throws RuntimeException;

    /**
     * Publish multiple in-memory events.
     *
     * @param eventsData the events to be published
     * @throws RuntimeException if the operation is interrupted
     */
    void publish(List<IEvent> eventsData) throws RuntimeException;

    /**
     * Publish an in-memory event based on a consumed event from the messaging system.
     *
     * @param eventEnvelope the event envelope containing the event data
     * @throws RuntimeException if the operation is interrupted
     */
    void publish(IEventEnvelopeBase eventEnvelope) throws RuntimeException;

    /**
     * Publish an in-memory event based on a consumed event from the messaging system.
     *
     * @param <T> the type of event data contained in the envelope
     * @param eventEnvelope the event envelope containing the event data
     * @throws RuntimeException if the operation is interrupted
     */
    <T> void publish(IEventEnvelope<T> eventEnvelope) throws RuntimeException;
}
