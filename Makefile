# Variables
PROJECT_GROUP := com.github.mehdihadeli
CATALOGS_API := $(PROJECT_GROUP):catalogs-api
CATALOGS := $(PROJECT_GROUP):catalogs
BUILDING_BLOCKS := $(PROJECT_GROUP):building-blocks

.PHONY: catalogs-build
catalogs-build:
	mvn package -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS)

.PHONY: catalogs-compile
catalogs-build:
	mvn compile -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS) -am

.PHONY: catalogs-dependency-tree
catalogs-dependency-tree:
	mvn dependency:tree -Dverbose -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS)

.PHONY: catalogs-install
catalogs-install:
	mvn install -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS) -am

# Run the API using mvn
# https://docs.spring.io/spring-boot/maven-plugin/run.html#run.run-goal
.PHONY: catalogs-run-spring
catalogs-run-spring:
	mvn spring-boot:run -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS)

# Clean build artifacts
.PHONY: catalogs-clean
catalogs-clean:
	mvn clean -pl $(CATALOGS_API) -pl $(CATALOGS) -pl $(BUILDING_BLOCKS)

.PHONY: install
install:
	mvn install

# Run with jar file with `java` command
.PHONY: run-jar-java
run-jar-java:
	java -jar ./java-food-delivery-microservices/target/java-food-delivery-microservices-0.0.1-SNAPSHOT.jar

# Run the API using mvn
# https://www.mojohaus.org/exec-maven-plugin/usage.html
# https://stackoverflow.com/questions/1089285/maven-run-project
.PHONY: run
run:
	mvn exec:java  -pl ./java-food-delivery-microservices

# Clean build artifacts
.PHONY: clean
clean:
	mvn clean

# https://dev.to/ankityadav33/standardize-code-formatting-with-spotless-2bdh
# https://github.com/diffplug/spotless/tree/main/plugin-maven
# check google style rules with spotless
.PHONY: check-style-spotless
check-style-spotless:
	mvn spotless:check

# apply google style rules with spotless
.PHONY: apply-style-spotless
apply-style-spotless:
	mvn spotless:apply

# apply migrations
.PHONY: flyway-migrate
flyway-migrate:
	mvn flyway:migrate