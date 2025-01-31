package com.github.mehdihadeli.buildingblocks.postgresmessagepersistence;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import com.github.mehdihadeli.buildingblocks.jpa.CustomJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(before = CoreAutoConfiguration.class, after = CustomJpaAutoConfiguration.class)
@Import(PostgresMessagePersistenceConfiguration.class)
public class PostgresMessagePersistenceAutoConfiguration {}
