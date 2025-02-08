package com.github.mehdihadeli.catalogs.api.fakes.products;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.api.fakes.productvariants.FakeProductVariant;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Size;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProduct;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeCreateProductV1 {
    public static CreateProduct generate() {
        Faker faker = new Faker();
        return new CreateProduct(
                new ProductId(UUID.randomUUID()),
                new CategoryId(UUID.randomUUID()),
                new Name(faker.commerce().productName()),
                new Price(
                        new BigDecimal(faker.commerce().price(100, 1000)),
                        faker.money().currency()),
                faker.options().option(ProductStatus.class),
                new Dimensions(
                        faker.random().nextLong(10, 100),
                        faker.random().nextLong(10, 100),
                        faker.random().nextLong(10, 100)),
                new Size(faker.size().adjective(), faker.science().unit()),
                FakeProductVariant.generate(2),
                null);
    }
}
