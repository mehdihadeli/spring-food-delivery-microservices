package com.github.mehdihadeli.catalogs.products.features.creatingproduct.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxCommand;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.validation.SpringValidator;
import com.github.mehdihadeli.catalogs.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.Product;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.products.domain.models.entities.ProductVariant;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Dimensions;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Price;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.products.domain.models.valueobjects.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Set;
import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record CreateProduct(
        ProductId productId,
        CategoryId categoryId,
        Name name,
        Price price,
        ProductStatus status,
        Dimensions dimensions,
        Size size,
        @Nullable Set<ProductVariant> initialVariants,
        @Nullable Description description)
        implements ITxCommand<CreateProductResult> {
    public CreateProduct {
        notBeNull(productId, "productId");
        notBeNull(categoryId, "categoryId");
        notBeNull(name, "name");
        notBeNull(price, "price");
        notBeNull(dimensions, "dimensions");
        notBeNull(size, "size");
    }
}

@Component
class CreateProductValidator extends SpringValidator<CreateProduct> {
    @Override
    public void validate(Object target, Errors errors) {
        CreateProduct command = (CreateProduct) target;

        if (command.productId() == null) {
            errors.rejectValue("productId", "productId.null", "Product ID must not be null");
        }

        if (command.categoryId() == null) {
            errors.rejectValue("categoryId", "categoryId.null", "Category ID must not be null");
        }

        if (command.name() == null) {
            errors.rejectValue("name", "name.null", "Name must not be null");
        }

        if (command.price() == null) {
            errors.rejectValue("price", "price.null", "Price must not be null");
        }

        if (command.dimensions() == null) {
            errors.rejectValue("dimensions", "dimensions.null", "Dimensions must not be null");
        }

        if (command.size() == null) {
            errors.rejectValue("size", "size.null", "Size must not be null");
        }
    }
}

@CommandHandler
class CreateProductHandler implements ICommandHandler<CreateProduct, CreateProductResult> {
    private final ProductAggregateRepository productAggregateRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateProductHandler.class);

    public CreateProductHandler(ProductAggregateRepository productAggregateRepository) {
        this.productAggregateRepository = productAggregateRepository;
    }

    @Override
    public CreateProductResult handle(CreateProduct command) {
        notBeNull(command, "command");

        // create product aggregate
        var product = Product.create(
                command.productId(),
                command.categoryId(),
                command.name(),
                command.price(),
                command.status(),
                command.dimensions(),
                command.size(),
                null,
                command.initialVariants(),
                command.description());

        var createdProduct = productAggregateRepository.add(product);

        // https://spring.io/blog/2024/08/23/structured-logging-in-spring-boot-3-4
        logger.atInfo()
                .addKeyValue("product", product)
                .addKeyValue("productId", product.getId().id())
                .log(
                        "product {} with id {} created successfully.",
                        product.getClass().getSimpleName(),
                        product.getId().id());

        return new CreateProductResult(createdProduct.getId().id());
    }
}

record CreateProductResult(UUID Id) {}
