package com.github.mehdihadeli.buildingblocks.core.messaging;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import java.util.UUID;
import org.springframework.lang.Nullable;

public class MessageMetadataAccessorImpl implements MessageMetadataAccessor {
    @Override
    public UUID correlationId() {
        // TODO: implement header propagation store
        return UlidCreator.getUlid().toUuid();
    }

    @Override
    public @Nullable UUID cautionId() {
        return UlidCreator.getUlid().toUuid();
    }
}
