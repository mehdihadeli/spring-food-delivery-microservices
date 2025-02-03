package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import com.github.mehdihadeli.buildingblocks.mediator.MediatorHandlerRegisterer;
import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MediatorHandlerRegisterer.class})
public @interface EnableMediatorHandlers {}
