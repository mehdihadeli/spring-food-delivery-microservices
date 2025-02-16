# Variables
PROJECT_GROUP := com.github.mehdihadeli
CATALOGS_API := $(PROJECT_GROUP):catalogs-api
CATALOGS := $(PROJECT_GROUP):catalogs
CATALOGS_API_DIR=services/catalogs/api
USERS_API_DIR=services/users/api
CUSTOMERS_API_DIR=services/customers/api
ORDERS_API_DIR=services/orders/api
CATALOGS_DIR=services/catalogs
CUSTOMERS_DIR=services/customers
USERS_DIR=services/users
ORDERS_DIR=services/orders
API_GATEWAY_DIR=api-gateway
BUILDING_BLOCKS := $(PROJECT_GROUP):building-blocks
BUILDING_BLOCKS_DIR := building-blocks

#################################################################
# Building-Blocks
#################################################################
.PHONY: building-blocks-dependency-tree
building-blocks-dependency-tree:
	@echo "Writing dependency tree..."
	@mvn -f $(BUILDING_BLOCKS_DIR)/pom.xml  dependency:tree -Dverbose
#	@mvn dependency:tree -Dverbose -pl $(BUILDING_BLOCKS_DIR)

# Build building-blocks
.PHONY: build-building-blocks
build-building-blocks:
	@echo "Building building-blocks..."
	@mvn -f $(BUILDING_BLOCKS_DIR)/pom.xml clean package
#	@mvn clean package -pl $(BUILDING_BLOCKS_DIR)

# Clean building-blocks
.PHONY: clean-building-blocks
clean-building-blocks:
	@echo "Cleaning building-blocks..."
	@mvn -f $(BUILDING_BLOCKS_DIR)/pom.xml clean
#	@mvn clean -pl $(BUILDING_BLOCKS_DIR)

# install building-blocks
.PHONY: install-building-blocks
install-building-blocks:
	@echo "Installing building-blocks..."
	@mvn -f $(BUILDING_BLOCKS_DIR)/pom.xml clean install
#	@mvn clean install -pl $(BUILDING_BLOCKS_DIR)

#################################################################
# Catalogs
#################################################################
.PHONY: catalogs-dependency-tree
catalogs-dependency-tree:
	@echo "Writing dependency tree..."
	@mvn -f $(CATALOGS_DIR)/pom.xml  dependency:tree -Dverbose
#	@mvn dependency:tree -Dverbose -pl $(CATALOGS_API_DIR)

# Run catalogs
.PHONY: run-catalogs
run-catalogs:
	@echo "Starting catalogs service..."
	@mvn -f $(CATALOGS_API_DIR)/pom.xml clean spring-boot:run
#	@mvn spring-boot:run -pl $(CATALOGS_API_DIR)

# Build catalogs
.PHONY: build-catalogs
build-catalogs:
	@echo "Building catalogs service..."
	@mvn -f $(CATALOGS_DIR)/pom.xml package
#	@mvn clean package -pl $(CATALOGS_API_DIR)

# Clean catalogs
.PHONY: clean-catalogs
clean-catalogs:
	@echo "Cleaning catalogs service..."
	@mvn -f $(CATALOGS_DIR)/pom.xml clean
#	@mvn clean -pl $(CATALOGS_API_DIR)

# install catalogs
.PHONY: install-catalogs
install-catalogs:
	@echo "Installing catalogs service..."
	@mvn -f $(CATALOGS_DIR)/pom.xml clean install
#	@mvn clean install -pl $(CATALOGS_API_DIR)

# test catalogs
.PHONY: test-catalogs
test-catalogs:
	@echo "Testing catalogs service..."
	@mvn -f $(CATALOGS_API_DIR)/pom.xml clean test
#	@mvn clean test -pl $(CATALOGS_API_DIR)

# apply migrations catalogs
.PHONY: flyway-migrate-catalogs
flyway-migrate-catalogs:
	@mvn -f $(CATALOGS_API_DIR)/pom.xml flyway:migrate

#################################################################
# Users
#################################################################
.PHONY: users-dependency-tree
users-dependency-tree:
	@echo "Writing dependency tree..."
	@mvn -f $(USERS_DIR)/pom.xml  dependency:tree -Dverbose
#	@mvn dependency:tree -Dverbose -pl $(USERS_API_DIR)

# Run users
.PHONY: run-users
run-users:
	@echo "Starting users service..."
	@mvn -f $(USERS_API_DIR)/pom.xml clean spring-boot:run
#	@mvn spring-boot:run -pl $(USERS_API_DIR)

# Build users
.PHONY: build-users
build-users:
	@echo "Building users service..."
	@mvn -f $(USERS_DIR)/pom.xml clean package
#	@mvn clean package -pl $(USERS_API_DIR)

# Clean users
.PHONY: clean-users
clean-users:
	@echo "Cleaning users service..."
	@mvn -f $(USERS_DIR)/pom.xml clean
#	@mvn clean -pl $(USERS_API_DIR)

# install users
.PHONY: install-users
install-users:
	@echo "Installing users service..."
	@mvn -f $(USERS_DIR)/pom.xml clean install
#	@mvn clean install -pl $(USERS_API_DIR)

# test users
.PHONY: test-users
test-users:
	@echo "Testing users service..."
	@mvn -f $(USERS_API_DIR)/pom.xml clean test
#	@mvn clean test -pl $(USERS_API_DIR)

# apply migrations users
.PHONY: flyway-migrate-users
flyway-migrate-users:
	@mvn -f $(USERS_API_DIR)/pom.xml flyway:migrate

#################################################################
# Api-Gateway
#################################################################

# Run api-gateway
.PHONY: run-api-gateway
run-api-gateway:
	@echo "Starting api-gateway..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml clean spring-boot:run
#	@mvn spring-boot:run -pl $(API_GATEWAY_DIR)

# Build api-gateway
.PHONY: build-api-gateway
build-api-gateway:
	@echo "Building api-gateway..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml clean package
#	@mvn clean package -pl $(API_GATEWAY_DIR)

# install api-gateway
.PHONY: install-api-gateway
install-api-gateway:
	@echo "Installing api-gateway..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml clean install
#	@mvn clean install -pl $(API_GATEWAY_DIR)

# Clean api-gateway
.PHONY: clean-api-gateway
clean-api-gateway:
	@echo "Cleaning api-gateway..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml clean
#	@mvn clean -pl $(API_GATEWAY_DIR)

# api-gateway dependency-tree
.PHONY: api-gateway-dependency-tree
api-gateway-dependency-tree:
	@echo "Writing dependency tree..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml  dependency:tree -Dverbose
#	@mvn dependency:tree -Dverbose -pl $(API_GATEWAY_DIR)

# test api-gateway
.PHONY: test-api-gateway
test-api-gateway:
	@echo "Testing api-gateway..."
	@mvn -f $(API_GATEWAY_DIR)/pom.xml clean test
#	@mvn clean test -pl $(API_GATEWAY_DIR)

#################################################################
# All Services
#################################################################

# install all microservices
.PHONY: install-all
install-all:
	@echo "Installing all microservices..."
	@mvn clean install

# build all microservices
# package does everything that `mvn package` does AND installs the built artifact into your local Maven repository (~/.m2/repository).
.PHONY: build-all
build-all:
	@echo "Building all microservices..."
	@mvn clean package

# Clean all microservices
.PHONY: clean-all
clean-all:
	@echo "Cleaning all microservices..."
	@mvn clean

# test all microservices
.PHONY: test-all
test-all:
	@echo "Testing all microservices..."
	@mvn clean test

# https://dev.to/ankityadav33/standardize-code-formatting-with-spotless-2bdh
# https://github.com/diffplug/spotless/tree/main/plugin-maven
# check style rules with spotless
.PHONY: check-spotless
check-spotless:
	@mvn spotless:check

# apply style rules with spotless
.PHONY: apply-spotless
apply-spotless:
	@mvn spotless:apply

# apply migrations
.PHONY: flyway-migrate
flyway-migrate:
	@mvn -f $(USERS_API_DIR)/pom.xml flyway:migrate
	@mvn -f $(CATALOGS_API_DIR)/pom.xml flyway:migrate
