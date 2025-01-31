package com.github.mehdihadeli.buildingblocks.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CustomMongoProperties.class)
@ConditionalOnClass({MongoTemplate.class})
@EnableMongoAuditing
public class MongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);

    // https://docs.spring.io/spring-data/mongodb/reference/mongodb/configuration.html
    @Bean
    @ConditionalOnMissingBean
    public MongoClient mongoClient(MongoProperties mongoProperties, CustomMongoProperties customMongoProperties) {
        ConnectionString connectionString = buildConnectionString(mongoProperties);

        // Build MongoClientSettings with custom properties
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> builder.maxSize(
                                customMongoProperties.getConnectionPool().getMaxSize())
                        .minSize(customMongoProperties.getConnectionPool().getMinSize()))
                .applyToSocketSettings(builder -> builder.connectTimeout(
                                customMongoProperties.getSocket().getConnectTimeout(), TimeUnit.MILLISECONDS)
                        .readTimeout(customMongoProperties.getSocket().getReadTimeout(), TimeUnit.MILLISECONDS))
                .uuidRepresentation(UuidRepresentation.STANDARD) // Set UUID representation for mongodb
                .build();

        return MongoClients.create(settings);
    }

    private static ConnectionString buildConnectionString(MongoProperties mongoProperties) {
        if (StringUtils.hasText(mongoProperties.getUri())) {
            return new ConnectionString(mongoProperties.getUri());
        }

        return new ConnectionString(constructMongoUri(mongoProperties));
    }

    private static String constructMongoUri(MongoProperties props) {
        StringBuilder uri = new StringBuilder("mongodb://");

        // Add credentials if present
        if (StringUtils.hasText(props.getUsername()) && props.getPassword() != null) {
            uri.append(URLEncoder.encode(props.getUsername(), StandardCharsets.UTF_8))
                    .append(":")
                    .append(URLEncoder.encode(new String(props.getPassword()), StandardCharsets.UTF_8))
                    .append("@");
        }

        // Add host and port
        if (StringUtils.hasText(props.getHost())) {
            uri.append(props.getHost() != null ? props.getHost() : "localhost")
                    .append(":")
                    .append(props.getPort() > 0 ? props.getPort() : 27017);

        } else {
            throw new IllegalArgumentException("MongoDB host must be specified");
        }
        // Add database if present
        if (StringUtils.hasText(props.getDatabase())) {
            uri.append("/").append(props.getDatabase());
        }

        // Add query parameters
        List<String> params = new ArrayList<>();

        if (StringUtils.hasText(props.getAuthenticationDatabase())) {
            params.add("authSource=" + props.getAuthenticationDatabase());
        }

        if (props.getReplicaSetName() != null) {
            params.add("replicaSet=" + props.getReplicaSetName());
        }

        if (!params.isEmpty()) {
            uri.append("?").append(String.join("&", params));
        }

        return uri.toString();
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoProperties mongoProperties) {
        return new MongoTemplate(mongoClient, mongoProperties.getDatabase());
    }
}
