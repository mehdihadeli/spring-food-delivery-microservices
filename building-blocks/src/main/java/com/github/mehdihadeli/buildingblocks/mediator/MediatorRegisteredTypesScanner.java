package com.github.mehdihadeli.buildingblocks.mediator;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.MediatorRegisteredTypes;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.MediatorScanResult;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.QueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.PipelineBehaviorComponent;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerComponent;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class MediatorRegisteredTypesScanner {
    private static final Set<AnnotationTypeFilter> mediatorTypeFilters = Set.of(
            new AnnotationTypeFilter(CommandHandler.class, false),
            new AnnotationTypeFilter(QueryHandler.class, false),
            new AnnotationTypeFilter(RequestHandlerComponent.class, false),
            new AnnotationTypeFilter(PipelineBehaviorComponent.class, false));

    private final ApplicationContext applicationContext;
    private final ResourcePatternResolver resourcePatternResolver;

    public MediatorRegisteredTypesScanner(ApplicationContext applicationContext, ResourceLoader resourceLoader) {
        this.applicationContext = applicationContext;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    public MediatorRegisteredTypes scan() throws IOException {
        var packagesToScan = getPackagesToScan(applicationContext);
        MediatorScanResult scanResult = new MediatorScanResult();

        for (String pkg : packagesToScan) {
            String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath(pkg) + "/**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory factory = new CachingMetadataReaderFactory(resourcePatternResolver);

            for (Resource resource : resources) {
                try {
                    MetadataReader reader = factory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    if (this.matchesMediatorTypeFilter(reader, factory)) {
                        scanResult.getRegisteredClassNames().add(className);
                    } else if (className.endsWith(".package-info")) {
                        scanResult
                                .getRegisteredPackages()
                                .add(className.substring(0, className.length() - ".package-info".length()));
                    }
                } catch (Throwable ex) {
                    throw new IOException("Error in reading mediator registered classes: " + resource, ex);
                }
            }
        }

        return scanResult.toMediatorRegisteredTypes();
    }

    private static String[] getPackagesToScan(BeanFactory beanFactory) {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }

        return StringUtils.toStringArray(packages);
    }

    private boolean matchesMediatorTypeFilter(MetadataReader reader, MetadataReaderFactory factory) throws IOException {
        for (TypeFilter filter : mediatorTypeFilters) {
            if (filter.match(reader, factory)) {
                return true;
            }
        }

        return false;
    }
}
