package com.github.mehdihadeli.buildingblocks.test;

import org.springframework.context.ApplicationContext;

public abstract class EndToEndTestBase extends IntegrationTestBase {
    public EndToEndTestBase(ApplicationContext applicationContext, String apiPrefix) {
        super(applicationContext);
        this.apiPrefix = apiPrefix;
    }
}
