package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import org.springframework.lang.Nullable;

import java.util.UUID;

public interface MessageMetadataAccessor {
    /// <summary>
    ///     Get CorrelationId from header storage and generate new if not exists.
    /// </summary>
    /// <returns>Guid.</returns>
    UUID correlationId();

    @Nullable
    UUID cautionId();
}