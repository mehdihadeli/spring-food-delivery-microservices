package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.EventEnvelopeMetadata;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;

// using `IEventEnvelopeMetadata` has problem in deserialize construction so we have to use `EventEnvelopeMetadata`
public record EventEnvelope<T>(T message, EventEnvelopeMetadata metadata) implements IEventEnvelope<T> {}
