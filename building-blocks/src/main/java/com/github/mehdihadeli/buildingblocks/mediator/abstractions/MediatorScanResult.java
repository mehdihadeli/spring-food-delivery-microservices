package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import java.util.ArrayList;
import java.util.List;

public class MediatorScanResult {

    private final List<String> registeredClassNames = new ArrayList<>();
    private final List<String> registeredPackages = new ArrayList<>();

    public MediatorRegisteredTypes toMediatorRegisteredTypes() {
        return new MediatorRegisteredTypes(this.getRegisteredClassNames(), this.getRegisteredPackages());
    }

    public List<String> getRegisteredClassNames() {
        return registeredClassNames;
    }

    public List<String> getRegisteredPackages() {
        return registeredPackages;
    }
}
