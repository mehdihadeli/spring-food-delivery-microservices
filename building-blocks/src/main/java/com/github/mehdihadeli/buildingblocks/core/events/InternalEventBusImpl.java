package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.InternalEventBus;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.events.IEvent;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InternalEventBusImpl implements InternalEventBus {
    private static final ConcurrentMap<Class<?>, Method> publishMethods = new ConcurrentHashMap<>();
    private final Mediator mediator;

    public InternalEventBusImpl(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void publish(IEvent eventData) throws RuntimeException {
        mediator.publish(eventData);
    }

    @Override
    public void publish(List<IEvent> eventsData) throws RuntimeException {
        eventsData.forEach(this::publish);
    }

    @Override
    public void publish(IEventEnvelopeBase eventEnvelope) throws RuntimeException {
        var genricMethod =
                publishMethods.computeIfAbsent(eventEnvelope.message().getClass(), eventType -> {
                    try {
                        return getGenericPublishMethod(eventType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        try {
            genricMethod.invoke(this, eventEnvelope);
        } catch (Exception e) {
            throw new RuntimeException("Error in publishing event envelope", e);
        }
    }

    @Override
    public <T> void publish(IEventEnvelope<T> eventEnvelope) throws RuntimeException {
        mediator.publish(eventEnvelope.message());
    }

    public static Method getGenericPublishMethod(Class<?> eventType) {
        return Arrays.stream(InternalEventBus.class.getDeclaredMethods()) // Get all declared methods
                .filter(method -> method.getName().equals("publish") // Filter by name
                        && method.getGenericParameterTypes().length > 0 // Ensure it has generic parameters
                        && method.getGenericParameterTypes()[0] instanceof ParameterizedType) // Check for generics
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Generic publish method not found"));
    }
}
