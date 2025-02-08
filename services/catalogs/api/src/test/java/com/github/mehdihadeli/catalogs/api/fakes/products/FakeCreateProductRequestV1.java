package com.github.mehdihadeli.catalogs.api.fakes.products;

import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProductRequest;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.*;

public class FakeCreateProductRequestV1 {
    public static CreateProductRequest generate() {
        Faker faker = new Faker();

        // Generate random UUID for categoryId
        UUID categoryId = UUID.randomUUID();

        // Generate random product name
        String name = faker.commerce().productName();

        // Generate random product status
        ProductStatus status = faker.options().option(ProductStatus.class);

        // Generate random price amount and currency
        BigDecimal priceAmount = new BigDecimal(faker.commerce().price(100, 1000));
        String currency = faker.money().currency();

        // Generate random dimensions (width, height, depth)
        double width = faker.random().nextDouble(10, 100);
        double height = faker.random().nextDouble(10, 100);
        double depth = faker.random().nextDouble(10, 100);

        // Generate random size and size unit
        String size = faker.size().adjective();
        String sizeUnit = faker.science().unit();

        // Generate random product variants (optional)
        Set<CreateProductRequest.ProductVariantRequest> productVariants = generateProductVariants(faker);

        // Generate random description (optional)
        String description = faker.lorem().sentence();

        return new CreateProductRequest(
                categoryId,
                name,
                status,
                priceAmount,
                currency,
                width,
                height,
                depth,
                size,
                sizeUnit,
                productVariants,
                description);
    }

    private static Set<CreateProductRequest.ProductVariantRequest> generateProductVariants(Faker faker) {
        Set<CreateProductRequest.ProductVariantRequest> variants = new HashSet<>();

        for (int i = 0; i < 2; i++) {
            String sku = faker.commerce().productName();
            BigDecimal amount = new BigDecimal(faker.commerce().price().replace(",", "."));
            String currency = faker.money().currency();
            int stock = faker.random().nextInt(10, 100);
            String color = faker.color().name();
            Map<String, String> attributes = generateAttributes(faker);

            CreateProductRequest.ProductVariantRequest variant =
                    new CreateProductRequest.ProductVariantRequest(sku, amount, currency, stock, color, attributes);

            variants.add(variant);
        }

        return variants;
    }

    private static Map<String, String> generateAttributes(Faker faker) {
        Map<String, String> attributes = new HashMap<>();
        int numberOfAttributes = faker.random().nextInt(1, 5);

        for (int i = 0; i < numberOfAttributes; i++) {
            String key = faker.lorem().word();
            String value = faker.lorem().sentence();
            attributes.put(key, value);
        }

        return attributes;
    }
}
