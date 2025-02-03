package com.github.mehdihadeli.buildingblocks.core.data;

import com.fasterxml.jackson.databind.util.Converter;
import com.mysema.commons.lang.Assert;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

// https://docs.spring.io/spring-data/mongodb/reference/mongodb/template-crud-operations.html

public final class MongoTemplateUtils {

    private MongoTemplateUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Finds a document by ID
     * @param mongoTemplate MongoTemplate instance
     * @param id Document ID
     * @param entityClass Entity class
     * @return Optional containing the found document or empty if not found
     */
    public static <T> Optional<T> findById(MongoTemplate mongoTemplate, String id, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(id, "ID must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        return Optional.ofNullable(mongoTemplate.findById(id, entityClass));
    }

    /**
     * Finds documents by a field value
     * @param mongoTemplate MongoTemplate instance
     * @param field Field name
     * @param value Field value
     * @param entityClass Entity class
     * @return List of matching documents
     */
    public static <T> List<T> findByField(
            MongoTemplate mongoTemplate, String field, Object value, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(field, "Field must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(Criteria.where(field).is(value));
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * Updates a single document matching the criteria
     * @param mongoTemplate MongoTemplate instance
     * @param criteria Query criteria
     * @param updates Map of field names to new values
     * @param entityClass Entity class
     * @return true if document was updated, false otherwise
     */
    public static <T> boolean updateFirst(
            MongoTemplate mongoTemplate, Criteria criteria, Map<String, Object> updates, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(criteria, "Criteria must not be null");
        Assert.notNull(updates, "Updates must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(criteria);
        Update update = new Update();
        updates.forEach(update::set);

        return mongoTemplate.updateFirst(query, update, entityClass).getModifiedCount() > 0;
    }

    /**
     * Updates multiple documents matching the criteria
     * @param mongoTemplate MongoTemplate instance
     * @param criteria Query criteria
     * @param updates Map of field names to new values
     * @param entityClass Entity class
     * @return Number of documents updated
     */
    public static <T> long updateMulti(
            MongoTemplate mongoTemplate, Criteria criteria, Map<String, Object> updates, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(criteria, "Criteria must not be null");
        Assert.notNull(updates, "Updates must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(criteria);
        Update update = new Update();
        updates.forEach(update::set);

        return mongoTemplate.updateMulti(query, update, entityClass).getModifiedCount();
    }

    /**
     * Deletes documents matching the criteria
     * @param mongoTemplate MongoTemplate instance
     * @param criteria Query criteria
     * @param entityClass Entity class
     * @return Number of documents deleted
     */
    public static <T> long deleteMatching(MongoTemplate mongoTemplate, Criteria criteria, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(criteria, "Criteria must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(criteria);
        return mongoTemplate.remove(query, entityClass).getDeletedCount();
    }

    /**
     * Finds documents with pagination and sorting
     * @param mongoTemplate MongoTemplate instance
     * @param criteria Query criteria
     * @param skip Number of documents to skip
     * @param limit Maximum number of documents to return
     * @param sort Sort definition
     * @param entityClass Entity class
     * @return List of matching documents
     */
    public static <T> List<T> findWithPagination(
            MongoTemplate mongoTemplate, Criteria criteria, int skip, int limit, Sort sort, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(criteria, "Criteria must not be null");
        Assert.isTrue(skip >= 0, "Skip must be non-negative");
        Assert.isTrue(limit > 0, "Limit must be positive");
        Assert.notNull(sort, "Sort must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(criteria).skip(skip).limit(limit).with(sort);

        return mongoTemplate.find(query, entityClass);
    }

    /**
     * Checks if any document exists matching the criteria
     * @param mongoTemplate MongoTemplate instance
     * @param criteria Query criteria
     * @param entityClass Entity class
     * @return true if matching document exists, false otherwise
     */
    public static <T> boolean exists(MongoTemplate mongoTemplate, Criteria criteria, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(criteria, "Criteria must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Query query = new Query(criteria);
        return mongoTemplate.exists(query, entityClass);
    }

    /**
     * Performs bulk insert of documents
     * @param mongoTemplate MongoTemplate instance
     * @param documents Collection of documents to insert
     * @param entityClass Entity class
     * @return Number of documents inserted
     */
    public static <T> int bulkInsert(MongoTemplate mongoTemplate, Collection<T> documents, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(documents, "Documents must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        Collection<T> insertedDocs = mongoTemplate.insertAll(documents);
        return insertedDocs.size();
    }

    public static <T> ExecutableFindOperation.ExecutableFind<T> query(
            MongoTemplate mongoTemplate, Class<T> entityClass) {
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");

        return mongoTemplate.query(entityClass);
    }

    /**
     * Creates a fluent query builder for the specified entity type
     * @param entityClass Entity class
     * @return QueryBuilder instance
     */
    public static <T> QueryBuilder<T> query(Class<T> entityClass) {
        // same as using
        return new QueryBuilder<>(entityClass);
    }

    // https://docs.spring.io/spring-data/mongodb/reference/mongodb/template-query-operations.html
    public static class QueryBuilder<T> {
        private final Class<T> entityClass;
        private final Query query;
        private MongoTemplate mongoTemplate;
        private static final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

        private QueryBuilder(Class<T> entityClass) {
            this.entityClass = entityClass;
            this.query = new Query();
        }

        /**
         * Sets the MongoTemplate for execution
         * @param mongoTemplate MongoTemplate instance
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> with(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
            return this;
        }

        /**
         * Adds a custom Query directly
         * @param customQuery Query instance
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> withQuery(Query customQuery) {
            // Copy criteria
            customQuery
                    .getQueryObject()
                    .forEach((key, value) ->
                            this.query.addCriteria(Criteria.where(key).is(value)));

            // Copy sort if present
            Document sortObject = customQuery.getSortObject();
            if (sortObject != null && !sortObject.isEmpty()) {
                this.query.with(convertToSort(sortObject));
            }

            // Copy field selection
            customQuery
                    .getFieldsObject()
                    .forEach((key, value) -> this.query.fields().include(key));

            return this;
        }

        /**
         * Converts MongoDB Document sort object to Spring Sort
         * @param sortDocument MongoDB sort document
         * @return Spring Sort object
         */
        private Sort convertToSort(Document sortDocument) {
            if (sortDocument == null || sortDocument.isEmpty()) {
                return Sort.unsorted();
            }

            List<Sort.Order> orders = new ArrayList<>();

            sortDocument.forEach((key, value) -> {
                // MongoDB sort: 1 for ascending, -1 for descending
                Sort.Direction direction = value.equals(1) ? Sort.Direction.ASC : Sort.Direction.DESC;
                orders.add(new Sort.Order(direction, key));
            });

            return Sort.by(orders);
        }

        /**
         * Includes specified fields in the result
         * @param fields Field names to include
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> include(String... fields) {
            for (String field : fields) {
                query.fields().include(field);
            }
            return this;
        }

        /**
         * Excludes specified fields from the result
         * @param fields Field names to exclude
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> exclude(String... fields) {
            for (String field : fields) {
                query.fields().exclude(field);
            }
            return this;
        }

        /**
         * Projects the result to include/exclude fields using a consumer
         * @param projectConsumer Consumer to configure projection
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> project(Consumer<Field> projectConsumer) {
            projectConsumer.accept(query.fields());
            return this;
        }

        /**
         * Adds raw criteria using MongoDB query syntax
         * @param key Field name
         * @param value Query value
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> raw(String key, Object value) {
            query.addCriteria(Criteria.where(key).is(value));
            return this;
        }

        /**
         * Adds an arbitrary criteria object
         * @param criteria Criteria instance
         * @return QueryBuilder instance
         */
        public QueryBuilder<T> matching(Criteria criteria) {
            query.addCriteria(criteria);
            return this;
        }

        // ... (previous comparison methods remain the same)

        /**
         * Executes the query with provided MongoTemplate
         * @param mongoTemplate MongoTemplate instance
         * @return List of matching documents
         */
        public List<T> execute(MongoTemplate mongoTemplate) {
            return mongoTemplate.find(query, entityClass);
        }

        /**
         * Executes the query if MongoTemplate was previously set
         * @return List of matching documents
         * @throws IllegalStateException if MongoTemplate not set
         */
        public List<T> execute() {
            Assert.notNull(mongoTemplate, "MongoTemplate must be set using with() or execute(MongoTemplate)");
            return mongoTemplate.find(query, entityClass);
        }
        /**
         * Projects query results to a different model class using Spring's ProjectionFactory
         * @param projectionClass Target projection interface
         * @return List of projected results
         */
        public <R> List<R> projectTo(Class<R> projectionClass) {
            Assert.notNull(mongoTemplate, "MongoTemplate must be set using with() or execute(MongoTemplate)");
            List<T> results = mongoTemplate.find(query, entityClass);
            return results.stream()
                    .map(entity -> projectionFactory.createProjection(projectionClass, entity))
                    .collect(Collectors.toList());
        }

        /**
         * Projects query results using a custom converter
         * @param targetClass Target class
         * @param converter Custom converter function
         * @return List of converted results
         */
        public <R> List<R> projectWithConverter(Class<R> targetClass, Converter<T, R> converter) {
            Assert.notNull(mongoTemplate, "MongoTemplate must be set using with() or execute(MongoTemplate)");
            List<T> results = mongoTemplate.find(query, entityClass);
            return results.stream().map(converter::convert).collect(Collectors.toList());
        }

        /**
         * Projects query results using a mapping function
         * @param mapper Function to map source to target
         * @return List of mapped results
         */
        public <R> List<R> projectWith(Function<T, R> mapper) {
            Assert.notNull(mongoTemplate, "MongoTemplate must be set using with() or execute(MongoTemplate)");
            List<T> results = mongoTemplate.find(query, entityClass);
            return results.stream().map(mapper).collect(Collectors.toList());
        }

        /**
         * Projects query results directly to target class using MongoTemplate's converter
         * @param targetClass Target class
         * @return List of converted results
         */
        public <R> List<R> projectToClass(Class<R> targetClass) {
            Assert.notNull(mongoTemplate, "MongoTemplate must be set using with() or execute(MongoTemplate)");
            return mongoTemplate.find(query, targetClass, mongoTemplate.getCollectionName(entityClass));
        }

        /**
         * Gets the underlying Query object
         * @return Query instance
         */
        public Query getQuery() {
            return this.query;
        }
    }
}
