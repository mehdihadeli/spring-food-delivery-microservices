CREATE TABLE categories
(
    id                 UUID                        NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by         UUID                        NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   UUID,
    is_deleted         BOOLEAN,
    deleted_date       TIMESTAMP WITHOUT TIME ZONE,
    deleted_by         UUID,
    version            INTEGER                     NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    code               VARCHAR(255)                NOT NULL,
    description        VARCHAR(255),
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE product_reviews
(
    id                 UUID                        NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by         UUID                        NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   UUID,
    is_deleted         BOOLEAN,
    deleted_date       TIMESTAMP WITHOUT TIME ZONE,
    deleted_by         UUID,
    version            INTEGER                     NOT NULL,
    product_id         UUID                        NOT NULL,
    customer_id        UUID                        NOT NULL,
    rating             INTEGER                     NOT NULL,
    comment            VARCHAR(1000)               NOT NULL,
    verified           BOOLEAN                     NOT NULL,
    helpful_votes      INTEGER                     NOT NULL,
    status             VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_product_reviews PRIMARY KEY (id)
);

CREATE TABLE product_variants
(
    id                 UUID                        NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by         UUID                        NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   UUID,
    is_deleted         BOOLEAN,
    deleted_date       TIMESTAMP WITHOUT TIME ZONE,
    deleted_by         UUID,
    version            INTEGER                     NOT NULL,
    product_id         UUID                        NOT NULL,
    sku                VARCHAR(255)                NOT NULL,
    stock              INTEGER                     NOT NULL,
    color              VARCHAR(255)                NOT NULL,
    money_amount       DECIMAL,
    money_currency     VARCHAR(255),
    CONSTRAINT pk_product_variants PRIMARY KEY (id)
);

CREATE TABLE products
(
    id                 UUID                        NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by         UUID                        NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   UUID,
    is_deleted         BOOLEAN,
    deleted_date       TIMESTAMP WITHOUT TIME ZONE,
    deleted_by         UUID,
    version            INTEGER                     NOT NULL,
    category_id        UUID                        NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    description        VARCHAR(255),
    status             VARCHAR(255)                NOT NULL,
    price_amount       DECIMAL,
    price_currency     VARCHAR(255),
    size_unit          VARCHAR(255),
    size_size          VARCHAR(255),
    dimensions_width   DOUBLE PRECISION,
    dimensions_height  DOUBLE PRECISION,
    dimensions_depth   DOUBLE PRECISION,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

ALTER TABLE product_variants
    ADD CONSTRAINT uc_product_variants_sku UNIQUE (sku);

ALTER TABLE product_reviews
    ADD CONSTRAINT FK_PRODUCT_REVIEWS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_variants
    ADD CONSTRAINT FK_PRODUCT_VARIANTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);