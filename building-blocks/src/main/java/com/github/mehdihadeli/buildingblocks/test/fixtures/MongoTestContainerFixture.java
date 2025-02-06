package com.github.mehdihadeli.buildingblocks.test.fixtures;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;

public class MongoTestContainerFixture {
    private static final Logger logger = LoggerFactory.getLogger(MongoTestContainerFixture.class);

    private final MongoDBContainer mongoContainer;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoTestContainerFixture() {
        // Initialize the MongoDB container
        this.mongoContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

        // Create a MongoClient to interact with the container
        this.mongoClient = MongoClients.create(mongoContainer.getConnectionString());

        // Ensure the database exists
        this.database = mongoClient.getDatabase("test_db");
    }

    /**
     * Starts the MongoDB container if it is not already running.
     */
    public void startContainer() {
        if (!mongoContainer.isRunning()) {
            mongoContainer.start();
            logger.info("MongoDB container started.");
        }
    }

    /**
     * Stops the MongoDB container.
     */
    public void stopContainer() {
        if (mongoContainer.isRunning()) {
            mongoContainer.stop();
            logger.info("MongoDB container stopped.");
        }
    }

    /**
     * Resets the database by dropping all collections.
     */
    public void resetDatabase() {
        var collectionNames = database.listCollectionNames();
        for (String collectionName : collectionNames) {
            database.getCollection(collectionName).drop();
            logger.info("Collection dropped: {}", collectionName);
        }
        logger.info("Database reset: all collections dropped.");
    }

    /**
     * Gets the MongoDB connection string.
     *
     * @return The connection string.
     */
    public String getConnectionString() {
        return mongoContainer.getConnectionString();
    }

    /**
     * Gets the MongoClient instance.
     *
     * @return The MongoClient.
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    /**
     * Gets the MongoDatabase instance.
     *
     * @return The MongoDatabase.
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Gets the database name.
     *
     * @return The database name.
     */
    public String getDatabaseName() {
        return database.getName();
    }
}
