package com.github.mehdihadeli.catalogs.core.shared.initializers;

import com.github.mehdihadeli.buildingblocks.abstractions.core.web.IApplicationReadyEventModuleListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@Component
public class SeedInitializer implements IApplicationReadyEventModuleListener {
    @Override
    public void Run(ApplicationReadyEvent applicationReadyEvent) {
        //        var env = beanFactory.getBean(ConfigurableEnvironment.class);
        //
        //        // Load providers using a BeanFactoryPostProcessor
        //        var productDataModelRepository = beanFactory.getBean(ProductDataModelRepository.class);
        //        System.out.println("Running seed database.....");
        //
        //        List<ProductDataModel> products =
        //                Instancio.ofList(ProductDataModel.class).size(10).create();
        //        productDataModelRepository.saveAll(products);
    }
}
