package com.github.mehdihadeli.buildingblocks.abstractions.core.request;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnit;

import java.util.UUID;

public interface IInternalCommand extends ICommandUnit {
    UUID internalCommandId();
}
