package com.github.mehdihadeli.catalogs.api;

import com.github.mehdihadeli.buildingblocks.core.web.ApplicationStartedListener;
import com.github.mehdihadeli.buildingblocks.core.web.ApplicationStoppedListener;
import com.github.mehdihadeli.catalogs.core.AppProperties;
import com.github.mehdihadeli.catalogs.core.CatalogsCoreRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// - `@ComponentScan` and `scanBasePackages` in `@SpringBootApplication` use for scanning configurations in `Java-Based
// @VerticalSliceTemplateConfiguration` which internally using @Component and `Annotation based configurations` like
// @Component, @Service
@SpringBootApplication(scanBasePackageClasses = {CatalogsCoreRoot.class})

// basePackages use for `auto-configurations` for scanning configurations like for `HibernateJpaAutoConfiguration` for
// scanning repositories and entities
@AutoConfigurationPackage(
        // `jpa auto-configuration` use `basePackages` which is application root package and all submodules as default
        // for finding repositories and entities. `VerticalSliceTemplateRoot` for finding repositories and interfaces
        basePackageClasses = {CatalogsCoreRoot.class})

// @ConfigurationPropertiesScan(basePackages = {Constants.ROOT_MODULE_NAME}) // configure all registered properties not
// one by one through @EnableConfigurationProperties
@EnableConfigurationProperties(AppProperties.class)
public class CatalogsApiApplication {
    // https://docs.spring.io/spring-framework/reference/core/spring-jcl.html
    // https://docs.spring.io/spring-boot/how-to/logging.html
    // slf4j `Logger` abstraction support structured logging but `spring-jcl` abstract `Log` doesn't support using
    // Logger is easier to mock instead of using `@Slf4j` annotation and using static `log` variable
    private static final Logger logger = LoggerFactory.getLogger(CatalogsApiApplication.class);

    public static void main(String[] args) {
        logger.atInfo().addKeyValue("app-name", Constants.APP_NAME).log("Starting {} application.", Constants.APP_NAME);

        SpringApplication app = new SpringApplication(CatalogsApiApplication.class);
        app.addListeners(new ApplicationStartedListener(startedEvent -> {
            logger.info("{} started.", Constants.APP_NAME);
        }));
        app.addListeners(new ApplicationStoppedListener(startedEvent -> {
            logger.info("{} stopped.", Constants.APP_NAME);
        }));

        app.run(args);
    }
}
