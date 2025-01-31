// package com.mehdihadeli.buildingblocks.mediator;
//
// import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.ImportSelector;
// import org.springframework.core.type.AnnotationMetadata;
//
//// registerers that implements `ImportBeanDefinitionRegistrar`, use for adding beans to the spring dependency
// container
//// at runtime
// @Configuration(proxyBeanMethods = false)
// @ConditionalOnProperty(prefix = "mediator", name = "enabled", havingValue = "true", matchIfMissing = true)
// @ConditionalOnMissingBean({MediatorConfiguration.class})
// public class MediatorHandlersImportSelector implements ImportSelector {
//    @Override
//    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//        return new String[] {determineImport()};
//    }
//
//    private String determineImport() {
//        return MediatorHandlerRegisterer.class.getName();
//    }
// }
