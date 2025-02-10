package com.github.mehdihadeli.buildingblocks.jpa;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@ConditionalOnClass({DataSource.class})
@EnableConfigurationProperties({CustomDataSourceProperties.class, CustomHibernateProperties.class})
// for setup connections for using in message persistence
@AutoConfiguration(
        before = {CoreAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({CustomJpaConfiguration.class})
public class CustomJpaAutoConfiguration {}
