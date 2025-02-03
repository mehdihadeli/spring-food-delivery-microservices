package com.github.mehdihadeli.catalogs.products;

import com.github.mehdihadeli.catalogs.products.data.entities.ProductDataModel;
import com.github.mehdihadeli.catalogs.products.dtos.ProductDto;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// https://github.com/mapstruct/mapstruct-examples/blob/main/mapstruct-field-mapping/src/main/java/org/mapstruct/example/mapper/OrderItemMapper.java
// https://mapstruct.org/documentation/stable/reference/html/

@Mapper
public interface ProductMapperMapStruct {
    ProductMapperMapStruct INSTANCE = Mappers.getMapper(ProductMapperMapStruct.class);

    // Map ProductDataModel to ProductDto
    ProductDto toProductDto(ProductDataModel productDataModel);

    default String fromOptionalString(Optional<String> optional) {
        return optional.orElse(null);
    }
}
