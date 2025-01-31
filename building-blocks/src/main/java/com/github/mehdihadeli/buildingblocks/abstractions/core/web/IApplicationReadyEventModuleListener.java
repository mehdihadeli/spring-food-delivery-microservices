package com.github.mehdihadeli.buildingblocks.abstractions.core.web;

import org.springframework.boot.context.event.ApplicationReadyEvent;

public interface IApplicationReadyEventModuleListener {
    void Run(ApplicationReadyEvent applicationReadyEvent);
}
