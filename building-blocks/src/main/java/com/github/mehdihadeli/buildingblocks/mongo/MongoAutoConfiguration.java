package com.github.mehdihadeli.buildingblocks.mongo;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
@ConditionalOnClass({MongoTemplate.class})
@EnableConfigurationProperties(CustomMongoProperties.class)
@Import(MongoConfiguration.class)
public class MongoAutoConfiguration {}
