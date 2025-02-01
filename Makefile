# Use gradlew.bat for Windows
MVN_EXEC=mvnw.cmd

# modules packages dependency tree
.PHONY: modules-dependency-tree
modules-dependency-tree:
	$(MVN_EXEC) dependency:tree -Dverbose

# api packages dependency tree
.PHONY: api-dependency-tree
api-dependency-tree:
	$(MVN_EXEC) dependency:tree -Dverbose -pl ./vertical-slice-template-api

# compile the project
.PHONY: compile
compile:
	$(MVN_EXEC) compile

# Build vertical-slice-template-api
.PHONY: build-vertical-slice-template-api
build-vertical-slice-template-api:
	$(MVN_EXEC)  compile -pl ./vertical-slice-template-api -am
	# $(MVN_EXEC) compile -pl com.mehdihadeli:vertical-slice-template-api -am

# Build vertical-slice-template
.PHONY: build-vertical-slice-template
build-vertical-slice-template:
	$(MVN_EXEC) compile -pl ./vertical-slice-template -am

# package the project to generate jar file
# we can now take a look in the ${project.basedir}/target directory and you will see the generated JAR file.
.PHONY: package
package:
	$(MVN_EXEC) package

# package vertical-slice-template-api to generate jar file
# we can now take a look in the ${project.basedir}/target directory and you will see the generated JAR file.
.PHONY: package-vertical-slice-template-api
package-vertical-slice-template-api:
	$(MVN_EXEC) package -pl ./vertical-slice-template-api -am
	# $(MVN_EXEC) package -pl com.mehdihadeli:vertical-slice-template-api -am

# package vertical-slice-template to generate jar file
# we can now take a look in the ${project.basedir}/target directory and you will see the generated JAR file.
.PHONY: package-vertical-slice-template
package-vertical-slice-template:
	$(MVN_EXEC) package -pl ./vertical-slice-template -am
	# $(MVN_EXEC) package -pl com.mehdihadeli:vertical-slice-template -am

.PHONY: install
install:
	$(MVN_EXEC) install

# install vertical-slice-template-api
.PHONY: install-vertical-slice-template-api
install-vertical-slice-template-api:
	$(MVN_EXEC) install -pl ./vertical-slice-template-api -am
	# $(MVN_EXEC) install -pl com.mehdihadeli:vertical-slice-template-api -am

# install vertical-slice-template
.PHONY: install-vertical-slice-template
install-vertical-slice-template:
	$(MVN_EXEC) install -pl ./vertical-slice-template -am
	# $(MVN_EXEC) install -pl com.mehdihadeli:vertical-slice-template -am

# Run with jar file with `java` command
.PHONY: run-jar-java
run-jar-java:
	java -jar ./vertical-slice-template-api/target/vertical-slice-template-api-0.0.1-SNAPSHOT.jar

# Run the API using mvn
# https://www.mojohaus.org/exec-maven-plugin/usage.html
# https://stackoverflow.com/questions/1089285/maven-run-project
.PHONY: run
run:
	$(MVN_EXEC) exec:java  -pl ./vertical-slice-template-api

# Run the API using mvn
# https://docs.spring.io/spring-boot/maven-plugin/run.html#run.run-goal
.PHONY: run-spring
run-spring:
	$(MVN_EXEC) spring-boot:run -pl ./vertical-slice-template-api

# Run the API using Gradle
.PHONY: run-vertical-slice-template-api
run-vertical-slice-template-api:
	$(MVN_EXEC) vertical-slice-template-api:run -i

# Clean build artifacts
.PHONY: clean
clean:
	$(MVN_EXEC) clean


# https://dev.to/ankityadav33/standardize-code-formatting-with-spotless-2bdh
# https://github.com/diffplug/spotless/tree/main/plugin-maven
# check google style rules with spotless
.PHONY: check-style-spotless
check-style-spotless:
	$(MVN_EXEC) spotless:check


# apply google style rules with spotless
.PHONY: apply-style-spotless
apply-style-spotless:
	$(MVN_EXEC) spotless:apply

# apply migrations
.PHONY: flyway-migrate
flyway-migrate:
	mvn flyway:migrate