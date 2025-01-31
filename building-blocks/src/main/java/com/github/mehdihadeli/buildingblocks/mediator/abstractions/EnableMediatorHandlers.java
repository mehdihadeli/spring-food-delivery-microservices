package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import com.github.mehdihadeli.buildingblocks.mediator.MediatorHandlerRegisterer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MediatorHandlerRegisterer.class})
public @interface EnableMediatorHandlers {}
