package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import java.util.List;

public record MediatorRegisteredTypes(List<String> registeredClassNames, List<String> registeredPackages) {}
