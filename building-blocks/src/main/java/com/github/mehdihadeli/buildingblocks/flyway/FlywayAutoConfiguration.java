package com.github.mehdihadeli.buildingblocks.flyway;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(FlywayConfiguration.class)
public class FlywayAutoConfiguration {}
