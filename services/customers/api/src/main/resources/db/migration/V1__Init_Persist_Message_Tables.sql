CREATE TABLE persist_messages
(
  id                 UUID         NOT NULL,
  created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by         UUID         NOT NULL,
  last_modified_date TIMESTAMP WITHOUT TIME ZONE,
  last_modified_by   UUID,
  is_deleted         BOOLEAN,
  deleted_date       TIMESTAMP WITHOUT TIME ZONE,
  deleted_by         UUID,
  version            INTEGER      NOT NULL,
  data_type          VARCHAR(255) NOT NULL,
  data               TEXT         NOT NULL,
  retry_count        INTEGER      NOT NULL,
  message_status     VARCHAR(255) NOT NULL,
  delivery_type      VARCHAR(255) NOT NULL,
  CONSTRAINT pk_persist_messages PRIMARY KEY (id)
);
