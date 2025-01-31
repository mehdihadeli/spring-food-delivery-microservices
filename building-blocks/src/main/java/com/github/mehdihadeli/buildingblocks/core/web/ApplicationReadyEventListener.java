package com.github.mehdihadeli.buildingblocks.core.web;

import com.github.mehdihadeli.buildingblocks.abstractions.core.web.IApplicationReadyEventModuleListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// we can add it to `addListeners` of `SpringApplication` or register it as a new bean with @Component
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        // Retrieve and run all modules
        var modules = applicationContext
                .getBeansOfType(IApplicationReadyEventModuleListener.class)
                .values();

        modules.forEach(module -> module.Run(event));
    }
}
