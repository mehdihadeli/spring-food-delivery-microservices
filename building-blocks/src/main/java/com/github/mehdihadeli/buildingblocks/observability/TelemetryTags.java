package com.github.mehdihadeli.buildingblocks.observability;

import java.util.HashMap;
import java.util.Map;

/**
 * Telemetry tags use for adding tags to activities as tag name
 */
public final class TelemetryTags {

    private TelemetryTags() {
        // Private constructor to prevent instantiation
    }

    // https://opentelemetry.io/docs/specs/semconv/general/trace/
    // https://opentelemetry.io/docs/specs/semconv/general/attribute-naming/
    public static final class Tracing {

        private Tracing() {
            // Private constructor to prevent instantiation
        }

        // https://opentelemetry.io/docs/specs/semconv/resource/#service
        // https://opentelemetry.io/docs/specs/semconv/attributes-registry/peer/#peer-attributes
        public static final class Service {

            private Service() {
                // Private constructor to prevent instantiation
            }

            public static final String PEER_SERVICE = "peer.service";
            public static final String NAME = "service.name";
            public static final String INSTANCE_ID = "service.instance.id";
            public static final String VERSION = "service.version";
            public static final String NAME_SPACE = "service.namespace";
        }

        // https://opentelemetry.io/docs/specs/semconv/attributes-registry/messaging/#general-messaging-attributes
        // https://opentelemetry.io/docs/specs/semconv/messaging/messaging-spans/
        public static final class Messaging {

            private Messaging() {
                // Private constructor to prevent instantiation
            }

            // https://opentelemetry.io/docs/specs/semconv/attributes-registry/messaging/#messaging-operation-type
            public static final class OperationType {

                private OperationType() {
                    // Private constructor to prevent instantiation
                }

                public static final String KEY = "messaging.operation.type";
                public static final String RECEIVE = "receive";
                public static final String SEND = "send";
                public static final String PROCESS = "process";
            }

            // https://opentelemetry.io/docs/specs/semconv/attributes-registry/messaging/#messaging-system
            public static final class System {

                private System() {
                    // Private constructor to prevent instantiation
                }

                public static final String KEY = "messaging.system";
                public static final String ACTIVE_MQ = "activemq";
                public static final String RABBIT_MQ = "rabbitmq";
                public static final String AWS_SQS = "aws_sqs";
                public static final String EVENT_GRID = "eventgrid";
                public static final String EVENT_HUBS = "eventhubs";
                public static final String GCP_PUB_SUB = "gcp_pubsub";
                public static final String KAFKA = "kafka";
                public static final String PULSAR = "pulsar";
                public static final String SERVICE_BUS = "servicebus";
            }

            public static final String DESTINATION = "messaging.destination";
            public static final String DESTINATION_KIND = "messaging.destination_kind";
            public static final String URL = "messaging.url";
            public static final String MESSAGE_ID = "messaging.message_id";
            public static final String CONVERSATION_ID = "messaging.conversation_id";
            public static final String CORRELATION_ID = "messaging.correlation_id";
            public static final String CAUSATION_ID = "messaging.causation_id";
            public static final String OPERATION = "messaging.operation";
            public static final String OPERATION_NAME = "messaging.operation.name";
            public static final String DESTINATION_NAME = "messaging.destination.name";
            public static final String CONSUMER_GROUP = "messaging.consumer.group.name";
            public static final String DESTINATION_PARTITION = "messaging.destination.partition.id";

            // https://opentelemetry.io/docs/specs/semconv/attributes-registry/messaging/#rabbitmq-attributes
            // https://opentelemetry.io/docs/specs/semconv/messaging/rabbitmq/
            public static final class RabbitMQ {

                private RabbitMQ() {
                    // Private constructor to prevent instantiation
                }

                public static final String ROUTING_KEY = "messaging.rabbitmq.destination.routing_key";
                public static final String DELIVERY_TAG = "messaging.rabbitmq.message.delivery_tag";

                public static Map<String, Object> producerTags(
                        String serviceName, String topicName, String routingKey, String deliveryTag) {
                    Map<String, Object> tags = new HashMap<>();
                    tags.put(System.KEY, System.KAFKA);
                    tags.put(DELIVERY_TAG, deliveryTag);
                    tags.put(DESTINATION, topicName);
                    tags.put(OperationType.KEY, OperationType.SEND);
                    tags.put(Service.NAME, serviceName);
                    tags.put(ROUTING_KEY, routingKey);
                    return tags;
                }

                public static Map<String, Object> consumerTags(
                        String serviceName, String topicName, String routingKey, String consumerGroup) {
                    Map<String, Object> tags = new HashMap<>();
                    tags.put(System.KEY, System.KAFKA);
                    tags.put(DESTINATION, topicName);
                    tags.put(OperationType.KEY, OperationType.RECEIVE);
                    tags.put(Service.NAME, serviceName);
                    tags.put(CONSUMER_GROUP, consumerGroup);
                    tags.put(ROUTING_KEY, routingKey);
                    return tags;
                }
            }

            // https://opentelemetry.io/docs/specs/semconv/attributes-registry/messaging/#kafka-attributes
            // https://opentelemetry.io/docs/specs/semconv/messaging/kafka/
            public static final class Kafka {

                private Kafka() {
                    // Private constructor to prevent instantiation
                }

                public static final String MESSAGE_KEY = "messaging.kafka.message.key";
                public static final String TOMBSTONE = "messaging.kafka.message.tombstone";
                public static final String OFFSET = "messaging.kafka.offset";

                public static Map<String, Object> producerTags(
                        String serviceName, String topicName, String messageKey) {
                    Map<String, Object> tags = new HashMap<>();
                    tags.put(System.KEY, System.KAFKA);
                    tags.put(DESTINATION, topicName);
                    tags.put(OperationType.KEY, OperationType.SEND);
                    tags.put(Service.NAME, serviceName);
                    tags.put(MESSAGE_KEY, messageKey);
                    return tags;
                }

                public static Map<String, Object> consumerTags(
                        String serviceName,
                        String topicName,
                        String messageKey,
                        String partitionName,
                        String consumerGroup) {
                    Map<String, Object> tags = new HashMap<>();
                    tags.put(System.KEY, System.KAFKA);
                    tags.put(DESTINATION, topicName);
                    tags.put(OperationType.KEY, OperationType.RECEIVE);
                    tags.put(Service.NAME, serviceName);
                    tags.put(MESSAGE_KEY, messageKey);
                    tags.put(DESTINATION_PARTITION, partitionName);
                    tags.put(CONSUMER_GROUP, consumerGroup);
                    return tags;
                }
            }
        }

        // https://opentelemetry.io/docs/specs/semconv/database/database-spans/#common-attributes
        // https://opentelemetry.io/docs/specs/semconv/database/postgresql/#attributes
        public static final class Db {

            private Db() {
                // Private constructor to prevent instantiation
            }

            public static final String SYSTEM = "db.system";
            public static final String CONNECTION_STRING = "db.connection_string";
            public static final String USER = "db.user";
            public static final String MS_SQL_INSTANCE_NAME = "db.mssql.instance_name";
            public static final String NAME = "db.name";
            public static final String STATEMENT = "db.statement";
            public static final String OPERATION = "db.operation";
            public static final String INSTANCE = "db.instance";
            public static final String URL = "db.url";
            public static final String CASSANDRA_KEYSPACE = "db.cassandra.keyspace";
            public static final String REDIS_DATABASE_INDEX = "db.redis.database_index";
            public static final String MONGO_DB_COLLECTION = "db.mongodb.collection";
        }

        // https://opentelemetry.io/docs/specs/semconv/exceptions/exceptions-spans/#exception-event
        public static final class Exception {

            private Exception() {
                // Private constructor to prevent instantiation
            }

            public static final String EVENT_NAME = "exception";
            public static final String TYPE = "exception.type";
            public static final String MESSAGE = "exception.message";
            public static final String STACKTRACE = "exception.stacktrace";
        }

        // https://opentelemetry.io/docs/specs/semconv/attributes-registry/otel/#otel-attributes
        public static final class Otel {

            private Otel() {
                // Private constructor to prevent instantiation
            }

            public static final String STATUS_CODE = "otel.status_code";
            public static final String STATUS_DESCRIPTION = "otel.status_description";
        }

        public static final class Message {

            private Message() {
                // Private constructor to prevent instantiation
            }

            public static final String TYPE = "message.type";
            public static final String ID = "message.id";
        }

        public static final class Application {

            private Application() {
                // Private constructor to prevent instantiation
            }

            public static final String APP_SERVICE = ObservabilityConstant.instrumentationName + ".appservice";
            public static final String CONSUMER = ObservabilityConstant.instrumentationName + ".consumer";
            public static final String PRODUCER = ObservabilityConstant.instrumentationName + ".producer";

            public static final class Commands {

                private Commands() {
                    // Private constructor to prevent instantiation
                }

                public static final String COMMAND = ObservabilityConstant.instrumentationName + ".command";
                public static final String COMMAND_TYPE = COMMAND + ".type";
                public static final String COMMAND_HANDLER = COMMAND + ".handler";
                public static final String COMMAND_HANDLER_TYPE = COMMAND_HANDLER + ".type";
            }

            public static final class Queries {

                private Queries() {
                    // Private constructor to prevent instantiation
                }

                public static final String QUERY = ObservabilityConstant.instrumentationName + ".query";
                public static final String QUERY_TYPE = QUERY + ".type";
                public static final String QUERY_HANDLER = QUERY + ".handler";
                public static final String QUERY_HANDLER_TYPE = QUERY_HANDLER + ".type";
            }

            public static final class Events {

                private Events() {
                    // Private constructor to prevent instantiation
                }

                public static final String EVENT = ObservabilityConstant.instrumentationName + ".event";
                public static final String EVENT_TYPE = EVENT + ".type";
                public static final String EVENT_HANDLER = EVENT + ".handler";
                public static final String EVENT_HANDLER_TYPE = EVENT_HANDLER + ".type";
            }
        }
    }

    // https://opentelemetry.io/docs/specs/semconv/general/metrics/
    // https://opentelemetry.io/docs/specs/semconv/general/attribute-naming/
    public static final class Metrics {

        private Metrics() {
            // Private constructor to prevent instantiation
        }

        public static final class Application {

            private Application() {
                // Private constructor to prevent instantiation
            }

            public static final String APP_SERVICE = ObservabilityConstant.instrumentationName + ".appservice";
            public static final String CONSUMER = ObservabilityConstant.instrumentationName + ".consumer";
            public static final String PRODUCER = ObservabilityConstant.instrumentationName + ".producer";

            public static final class Commands {

                private Commands() {
                    // Private constructor to prevent instantiation
                }

                public static final String COMMAND = ObservabilityConstant.instrumentationName + ".command";
                public static final String COMMAND_TYPE = COMMAND + ".type";
                public static final String COMMAND_HANDLER = COMMAND + ".handler";
                public static final String SUCCESS_COUNT = COMMAND_HANDLER + ".success.count";
                public static final String FAILED_COUNT = COMMAND_HANDLER + ".failed.count";
                public static final String ACTIVE_COUNT = COMMAND_HANDLER + ".active.count";
                public static final String TOTAL_EXECUTED_COUNT = COMMAND_HANDLER + ".total.count";
                public static final String HANDLER_DURATION = COMMAND_HANDLER + ".duration";
            }

            public static final class Queries {

                private Queries() {
                    // Private constructor to prevent instantiation
                }

                public static final String QUERY = ObservabilityConstant.instrumentationName + ".query";
                public static final String QUERY_TYPE = QUERY + ".type";
                public static final String QUERY_HANDLER = QUERY + ".handler";
                public static final String SUCCESS_COUNT = QUERY_HANDLER + ".success.count";
                public static final String FAILED_COUNT = QUERY_HANDLER + ".failed.count";
                public static final String ACTIVE_COUNT = QUERY_HANDLER + ".active.count";
                public static final String TOTAL_EXECUTED_COUNT = QUERY_HANDLER + ".total.count";
                public static final String HANDLER_DURATION = QUERY_HANDLER + ".duration";
            }

            public static final class Events {

                private Events() {
                    // Private constructor to prevent instantiation
                }

                public static final String EVENT = ObservabilityConstant.instrumentationName + ".event";
                public static final String EVENT_TYPE = EVENT + ".type";
                public static final String EVENT_HANDLER = EVENT + ".handler";
                public static final String SUCCESS_COUNT = EVENT_HANDLER + ".success.count";
                public static final String FAILED_COUNT = EVENT_HANDLER + ".failed.count";
                public static final String ACTIVE_COUNT = EVENT_HANDLER + ".active.count";
                public static final String TOTAL_EXECUTED_COUNT = EVENT_HANDLER + ".total.count";
                public static final String HANDLER_DURATION = EVENT_HANDLER + ".duration";
            }
        }
    }
}