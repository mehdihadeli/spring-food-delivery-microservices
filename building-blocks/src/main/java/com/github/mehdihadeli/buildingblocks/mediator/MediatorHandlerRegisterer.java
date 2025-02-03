package com.github.mehdihadeli.buildingblocks.mediator;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.EnableMediatorHandlers;
import java.lang.annotation.Annotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

// registerers that implements `ImportBeanDefinitionRegistrar`, use for adding beans to the spring dependency container
// at runtime
public class MediatorHandlerRegisterer
        implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {
        // registering handlers in run-time
        // registry.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.registerBeanDefinitions(importingClassMetadata, registry, (BeanNameGenerator) null);
    }

    protected Class<? extends Annotation> getAnnotation() {
        return EnableMediatorHandlers.class;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
