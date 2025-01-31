package com.github.mehdihadeli.buildingblocks.jpa;

import com.github.mehdihadeli.buildingblocks.core.utils.SpringBeanUtils;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AggregatesDomainEventsStorageInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AuditInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.DeleteInterceptor;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.ManagedClassNameFilter;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// root path from project resources
// @PropertySource("classpath:/jpa.yml")
@Configuration
@ConditionalOnClass({DataSource.class})
@EnableConfigurationProperties({CustomDataSourceProperties.class, CustomHibernateProperties.class})
// ensures that values for @CreatedBy, @CreatedDate, @LastModifiedBy, and @LastModifiedDate are automatically set when
// an entity is persisted or updated.
@EnableJpaAuditing
@EnableTransactionManagement
public class CustomJpaConfiguration {

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    CustomJpaConfiguration(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    //    @Bean
    //    public void enableSoftDeleteFilter(EntityManager entityManager) {
    //        var session = entityManager.unwrap(Session.class);
    //        session.enableFilter("deletedFilter").setParameter("isDeleted", false);
    //    }

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter(CustomDataSourceProperties customDataSourceProperties) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(jpaProperties.isShowSql());

        if (jpaProperties.getDatabase() != null) {
            adapter.setDatabase(jpaProperties.getDatabase());
        } else {
            // - based on type we JpaVendorAdapter generate `hibernate.dialect` property for specific database type
            // - also we need type for generating hibernate `vendorProperties` based on this database type
            throw new IllegalArgumentException("`database type` not set properly.");
        }

        if (jpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        }

        if (customDataSourceProperties.isUseInMemory()) {
            adapter.setGenerateDdl(true);
        } else {
            adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        }

        return adapter;
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(
            DataSourceProperties dataSourceProperties, CustomDataSourceProperties customDataSourceProperties) {
        DataSource dataSource;
        if (!StringUtils.hasText(dataSourceProperties.getUrl()) && customDataSourceProperties.isUseInMemory()) {
            var jdbcUrl = "jdbc:h2:mem:localdb";
            dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                    .type(HikariDataSource.class)
                    .url(jdbcUrl)
                    .build();
        } else {
            var jdbcConnectionDetails = new CustomJdbcConnectionDetails(dataSourceProperties);
            dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                    .type(HikariDataSource.class)
                    .driverClassName(jdbcConnectionDetails.getDriverClassName())
                    .url(jdbcConnectionDetails.getJdbcUrl())
                    .username(jdbcConnectionDetails.getUsername())
                    .password(jdbcConnectionDetails.getPassword())
                    .build();
        }

        ValidationUtils.notBeNull(dataSource, "dataSource");
        HikariConfig hikariConfig = getHikariConfig(dataSource);

        return new HikariDataSource(hikariConfig);
    }

    private static HikariConfig getHikariConfig(DataSource datasource) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(datasource);

        if (hikariConfig.getMaxLifetime() <= 0) {
            hikariConfig.setMaxLifetime(HikariConfigConstants.MAX_LIFETIME);
        }
        if (hikariConfig.getMaximumPoolSize() <= 0) {
            hikariConfig.setMaximumPoolSize(HikariConfigConstants.MAX_POOL_SIZE);
        }
        if (hikariConfig.getMinimumIdle() <= 0) {
            hikariConfig.setMinimumIdle(HikariConfigConstants.MIN_IDLE);
        }
        if (hikariConfig.getIdleTimeout() <= 0) {
            hikariConfig.setIdleTimeout(HikariConfigConstants.IDLE_TIMEOUT);
        }
        if (hikariConfig.getConnectionTimeout() <= 0) {
            hikariConfig.setConnectionTimeout(HikariConfigConstants.CONNECTION_TIMEOUT);
        }
        if (hikariConfig.getValidationTimeout() <= 0) {
            hikariConfig.setValidationTimeout(HikariConfigConstants.VALIDATION_TIMEOUT);
        }
        if (hikariConfig.getLeakDetectionThreshold() <= 0) {
            hikariConfig.setLeakDetectionThreshold(HikariConfigConstants.LEAK_DETECTION_THRESHOLD);
        }

        return hikariConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            JpaVendorAdapter jpaVendorAdapter,
            CustomHibernateProperties customHibernateProperties,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        return new EntityManagerFactoryBuilder(
                jpaVendorAdapter, this.buildJpaProperties(customHibernateProperties), (PersistenceUnitManager)
                        persistenceUnitManager.getIfAvailable());
    }

    // - Beans like `EntityManagerFactoryBuilder` and `PersistenceManagedTypes` are only available when Spring Boot's
    // auto-configuration mechanism is active, are not directly registered in the application context by Spring Core.
    // Instead, they are part of Spring Boot's JPA auto-configuration.
    // - Auto-configuration ensures that these beans are only created when the required dependencies (like
    // spring-boot-starter-data-jpa) are present.
    // - Without `auto-configuration` and using `Component Scanning` , we need to manually instantiate and configure
    // these beans, which can be error-prone and tightly coupled to internal implementations
    @Primary
    @Bean
    @ConditionalOnBean(PersistenceManagedTypes.class)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            ApplicationContext applicationContext,
            DataSource dataSource,
            EntityManagerFactoryBuilder factoryBuilder,
            PersistenceManagedTypes persistenceManagedTypes) {
        var localContainerEntityManagerFactoryBean = factoryBuilder
                .dataSource(dataSource)
                // for setting `PersistManagedTypes` in `LocalContainerEntityManagerFactoryBean`
                .managedTypes(persistenceManagedTypes)
                // for setting PackageToScan in `LocalContainerEntityManagerFactoryBean`. it uses
                // `PersistenceManagedTypesScanner.scan` for creating `PersistManagedTypes` from `PackageToScan` and its
                // submodules
                .packages(SpringBeanUtils.getAutoConfigurationPackages(applicationContext)
                        .toArray(new String[0]))
                // setJpaVendorAdapter will call inside of build with injected `JpaVendorAdapter` and using
                // `JpaBaseConfiguration.jpaVendorAdapter` as default registered vendor
                // for setting `PersistManagedTypes` in `LocalContainerEntityManagerFactoryBean`
                // for setting PackageToScan in `LocalContainerEntityManagerFactoryBean`. it uses
                // `PersistenceManagedTypesScanner.scan` for creating `PersistManagedTypes`
                .build();

        return localContainerEntityManagerFactoryBean;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    static class PersistenceManagedTypesConfiguration {
        PersistenceManagedTypesConfiguration() {}

        @Bean
        @Primary
        @ConditionalOnMissingBean
        static PersistenceManagedTypes persistenceManagedTypes(
                BeanFactory beanFactory,
                ResourceLoader resourceLoader,
                ObjectProvider<ManagedClassNameFilter> managedClassNameFilter) {
            String[] packagesToScan = getPackagesToScan(beanFactory);
            return (new PersistenceManagedTypesScanner(
                            resourceLoader, (ManagedClassNameFilter) managedClassNameFilter.getIfAvailable()))
                    .scan(packagesToScan);
        }

        private static String[] getPackagesToScan(BeanFactory beanFactory) {
            List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
            if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
                packages = AutoConfigurationPackages.get(beanFactory);
            }

            return StringUtils.toStringArray(packages);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    AuditInterceptor auditInterceptor() {
        return new AuditInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    DeleteInterceptor deleteInterceptor() {
        return new DeleteInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    AggregatesDomainEventsStorageInterceptor aggregatesDomainEventsStorageInterceptor() {
        return new AggregatesDomainEventsStorageInterceptor();
    }

    private Map<String, ?> buildJpaProperties(CustomHibernateProperties customHibernateProperties) {
        // Start with JPA properties
        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());

        // Add Hibernate properties with existing properties for DDL determination
        properties.putAll(customHibernateProperties.determineHibernateProperties(
                properties.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))),
                hibernateProperties));

        // Get and customize vendor properties
        Map<String, Object> vendorProperties = getVendorProperties(properties);

        // Add vendor properties last to allow overrides
        properties.putAll(vendorProperties);

        // additional properties
        properties.putAll(additionalProperties());

        return properties;
    }

    protected Map<String, Object> getVendorProperties(Map<String, Object> existingProperties) {
        Map<String, Object> properties = new HashMap<>();
        Database database = jpaProperties.getDatabase();

        if (database != null) {
            switch (database) {
                case POSTGRESQL:
                    if (existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == null
                            || existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == "none") {
                        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
                    }
                    break;
                case MYSQL:
                    if (existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == null
                            || existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == "none") {
                        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
                    }
                    break;
                case H2:
                    if (existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == null
                            || existingProperties.get(AvailableSettings.HBM2DDL_AUTO) == "none") {
                        properties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
                    }
                default:
                    break;
            }
        }

        return properties;
    }

    protected Map<String, Object> additionalProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(EntityManagerFactoryBuilderImpl.INTEGRATOR_PROVIDER, (IntegratorProvider)
                () -> List.of(RootAwareEventListenerIntegrator.INSTANCE));

        return properties;
    }
}
