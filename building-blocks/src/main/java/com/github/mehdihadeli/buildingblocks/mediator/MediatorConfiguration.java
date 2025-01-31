package com.github.mehdihadeli.buildingblocks.mediator;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

// https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html#features.developing-auto-configuration.condition-annotations

// when `proxyBeanMethods = false`, avoids the direct method call problem that would occur when one @Bean method calls
// another internally.
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MediatorProperties.class)
@ConditionalOnClass({Mediator.class})
@ConditionalOnProperty(prefix = "mediator", name = "enabled", havingValue = "true", matchIfMissing = true)
// @Import(MediatorHandlersImportSelector.class)
public class MediatorConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Mediator mediator(ApplicationContext applicationContext) {
        return new MediatorImpl(applicationContext);
    }

    //    @Configuration(proxyBeanMethods = false)
    //    @ConditionalOnMissingBean({MediatorRegisteredTypesConfiguration.class, MediatorConfiguration.class})
    //    static class MediatorRegisteredTypesConfiguration {
    //        MediatorRegisteredTypesConfiguration() {}
    //
    //        @Bean
    //        @Scope("singleton")
    //        @ConditionalOnMissingBean
    //        static MediatorRegisteredTypesScanner mediatorRegisteredTypesScanner(
    //                ApplicationContext applicationContext, ResourceLoader resourceLoader) {
    //            return (new MediatorRegisteredTypesScanner(applicationContext, resourceLoader));
    //        }
    //
    //        @Bean
    //        @Scope("singleton")
    //        @ConditionalOnMissingBean
    //        static MediatorRegisteredTypes mediatorRegisteredTypes(MediatorRegisteredTypesScanner scanner)
    //                throws IOException {
    //            return scanner.scan();
    //        }
    //    }
}
