package com.github.mehdihadeli.buildingblocks.core.id;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;

import java.util.UUID;

public class UlIdIdGenerator implements IdGenerator {
    @Override
    public UUID generateId() {
        return UlidCreator.getUlid().toUuid();
    }
}
