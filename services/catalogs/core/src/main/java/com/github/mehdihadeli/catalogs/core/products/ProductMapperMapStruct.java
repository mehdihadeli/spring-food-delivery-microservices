package com.github.mehdihadeli.catalogs.core.products;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// https://github.com/mapstruct/mapstruct-examples/blob/main/mapstruct-field-mapping/src/main/java/org/mapstruct/example/mapper/OrderItemMapper.java
// https://mapstruct.org/documentation/stable/reference/html/

@Mapper
public interface ProductMapperMapStruct {
    ProductMapperMapStruct INSTANCE = Mappers.getMapper(ProductMapperMapStruct.class);
}
