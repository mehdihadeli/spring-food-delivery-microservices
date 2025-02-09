package com.github.mehdihadeli.catalogs.api.fakes.productvariants;

import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Money;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductVariant;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Color;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.SKU;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.Stock;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.VariantId;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeProductVariant {
    public static Set<ProductVariant> generate(int numberOfVariants) {
        Faker faker = new Faker();

        List<ProductVariant> result = faker.<ProductVariant>collection()
                .suppliers(() -> new ProductVariant(
                        new VariantId(UUID.randomUUID()),
                        new SKU(faker.commerce().productName()),
                        new Money(
                                new BigDecimal(faker.commerce().price().replace(",", ".")),
                                faker.money().currency()),
                        new Stock(faker.random().nextInt(10, 100)),
                        new Color(faker.color().name())))
                .maxLen(numberOfVariants)
                .generate();

        return new HashSet<>(result);
    }
}
