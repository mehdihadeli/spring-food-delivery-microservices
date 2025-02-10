package com.github.mehdihadeli.catalogs.core.products;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Money;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductReviewDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductVariantDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.projections.ProductReviewProjection;
import com.github.mehdihadeli.catalogs.core.products.data.projections.ProductSummaryProjection;
import com.github.mehdihadeli.catalogs.core.products.data.projections.ProductVariantProjection;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductReadModel;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductReviewReadModel;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductVariantReadModel;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.DimensionsVO;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.MoneyVO;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.PriceVO;
import com.github.mehdihadeli.catalogs.core.products.data.valueobjects.SizeVO;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.Product;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductReview;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductStatus;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.ProductVariant;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.*;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductDto;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductInfoDto;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductReviewDto;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductVariantDto;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProduct;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProductRequest;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.domain.ProductCreated;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ProductMapper {
    private ProductMapper() {}

    public static Product toProductAggregate(ProductDataModel productDataModel) {
        var productId = new ProductId(productDataModel.getId());
        var categoryId = new CategoryId(productDataModel.getCategoryId());
        String categoryName = productDataModel.getCategory() != null
                ? productDataModel.getCategory().getName()
                : null;
        var name = new Name(productDataModel.getName());
        var state = ProductStatus.valueOf(productDataModel.getStatus().name());
        var price = new Price(
                productDataModel.getPrice().getAmount(),
                productDataModel.getPrice().getCurrency());
        var size = new Size(
                productDataModel.getSize().getSize(), productDataModel.getSize().getUnit());
        var dimensions = new Dimensions(
                productDataModel.getDimensions().getWidth(),
                productDataModel.getDimensions().getHeight(),
                productDataModel.getDimensions().getDepth());
        var description =
                productDataModel.getDescription() != null ? new Description(productDataModel.getDescription()) : null;
        List<ProductReview> reviewList = null;
        Set<ProductVariant> variantList = null;
        if (productDataModel.getReviews() != null) {
            reviewList = toProductReviews(productDataModel.getReviews());
        }
        if (productDataModel.getVariants() != null) {
            variantList = toProductVariants(productDataModel.getVariants());
        }
        return Product.create(
                productId, categoryId, name, price, state, dimensions, size, reviewList, variantList, description);
    }

    public static ProductDataModel toProductDataModel(Product product) {
        return toProductDataModel(product, null);
    }

    public static ProductDataModel toProductDataModel(Product product, ProductDataModel existingDataModel) {
        ProductDataModel productDataModel = existingDataModel == null ? new ProductDataModel() : existingDataModel;

        productDataModel.setId(product.getId().id());
        productDataModel.setCategoryId(product.getCategoryId().id());
        productDataModel.setName(product.getName().value());
        productDataModel.setStatus(ProductStatus.valueOf(product.getStatus().name()));
        productDataModel.setDescription(
                product.getDescription().map(Description::value).orElse(null));
        productDataModel.setSize(
                new SizeVO(product.getSize().size(), product.getSize().unit()));
        productDataModel.setDimensions(new DimensionsVO(
                product.getDimensions().width(),
                product.getDimensions().height(),
                product.getDimensions().depth()));
        productDataModel.setPrice(
                new PriceVO(product.getPrice().amount(), product.getPrice().currency()));

        List<ProductReviewDataModel> reviewList = null;
        Set<ProductVariantDataModel> variantList = null;
        if (productDataModel.getReviews() != null) {
            reviewList = toProductReviewsDataModel(product.getReviews(), productDataModel);
        }
        if (productDataModel.getVariants() != null) {
            variantList = toProductVariantsDataModel(product.getVariants(), productDataModel);
        }
        productDataModel.setVariants(variantList);
        productDataModel.setReviews(reviewList);
        productDataModel.addDomainEvents(product.dequeueUncommittedDomainEvents());

        return productDataModel;
    }

    public static ProductDto toProductDto(Product product) {
        return new ProductDto(
                product.getId().id(),
                product.getCategoryId().id(),
                product.getName().value(),
                product.getStatus(),
                product.getPrice().amount(),
                product.getPrice().currency(),
                product.getDimensions().width(),
                product.getDimensions().height(),
                product.getDimensions().depth(),
                toProductVariantsDto(product.getVariants()),
                toProductReviewsDto(product.getReviews()),
                product.getDescription().map(Description::value).orElse(null));
    }

    public static ProductInfoDto toProductInfoDto(ProductSummaryProjection projection) {
        if (projection == null) {
            return null;
        }

        return new ProductInfoDto(
                projection.getId(),
                projection.getCategoryId(),
                projection.getName(),
                projection.getPrice().getAmount(),
                projection.getSize().getSize(),
                projection.getStatus(),
                projectionToProductVariantsDto(projection.getVariants()),
                projectionToProductReviewsDto(projection.getReviews()));
    }

    public static Product toProductAggregate(CreateProduct createProduct) {
        return Product.create(
                createProduct.productId(),
                createProduct.categoryId(),
                createProduct.name(),
                createProduct.price(),
                createProduct.status(),
                createProduct.dimensions(),
                createProduct.size(),
                null,
                createProduct.initialVariants(),
                createProduct.description());
    }

    // product variant
    public static ProductVariantDataModel toProductVariantDataModel(
            ProductVariant productVariant, ProductDataModel productDataModel) {
        if (productVariant == null) {
            return null;
        }

        ProductVariantDataModel dataModel = new ProductVariantDataModel();
        dataModel.setProduct(productDataModel);
        dataModel.setId(productVariant.getId().id());
        dataModel.setSku(productVariant.getSku().value());
        dataModel.setPrice(new MoneyVO(
                productVariant.getPrice().amount(), productVariant.getPrice().currency()));
        dataModel.setStock(productVariant.getStock().quantity());
        dataModel.setColor(productVariant.getColor().value());

        return dataModel;
    }

    public static ProductVariant toProductVariant(ProductVariantDataModel dataModel) {
        if (dataModel == null) {
            return null;
        }

        return new ProductVariant(
                new VariantId(dataModel.getId()),
                new SKU(dataModel.getSku()),
                new Money(dataModel.getPrice().getAmount(), dataModel.getPrice().getCurrency()),
                new Stock(dataModel.getStock()),
                new Color(dataModel.getColor()));
    }

    public static Set<ProductVariant> toProductVariants(Set<ProductVariantDataModel> dataModels) {
        return dataModels.stream().map(ProductMapper::toProductVariant).collect(Collectors.toSet());
    }

    public static Set<ProductVariantDataModel> toProductVariantsDataModel(
            Set<ProductVariant> productVariants, ProductDataModel productDataModel) {
        if (productVariants == null) {
            return Collections.emptySet();
        }

        return productVariants.stream()
                .map(productVariant -> toProductVariantDataModel(productVariant, productDataModel))
                .collect(Collectors.toSet());
    }

    public static Set<ProductVariant> toProductVariants(
            Set<CreateProductRequest.ProductVariantRequest> dtoSet, IdGenerator idGenerator) {
        if (dtoSet == null) {
            return Collections.emptySet();
        }

        return dtoSet.stream()
                .map(productVariantDto -> toProductVariant(productVariantDto, idGenerator))
                .collect(Collectors.toSet());
    }

    private static ProductVariant toProductVariant(
            CreateProductRequest.ProductVariantRequest dto, IdGenerator idGenerator) {

        if (dto == null) {
            return null;
        }

        VariantId variantId = new VariantId(idGenerator.generateId());
        SKU sku = new SKU(dto.sku());
        Money price = new Money(dto.amount(), dto.currency());
        Stock stock = new Stock(dto.stock());
        Color color = new Color(dto.color());

        return new ProductVariant(variantId, sku, price, stock, color);
    }

    public static Set<ProductVariantDto> toProductVariantsDto(Set<ProductVariant> variants) {
        if (variants == null) {
            return Collections.emptySet();
        }
        return variants.stream().map(ProductMapper::toProductVariantDto).collect(Collectors.toSet());
    }

    public static ProductVariantDto toProductVariantDto(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        return new ProductVariantDto(
                productVariant.getId().id(),
                productVariant.getSku().value(),
                productVariant.getPrice().amount(),
                productVariant.getPrice().currency(),
                productVariant.getStock().quantity(),
                productVariant.getColor().value());
    }

    public static Set<ProductVariantDto> projectionToProductVariantsDto(
            Set<ProductVariantProjection> variantsProjection) {
        if (variantsProjection == null) {
            return Collections.emptySet();
        }
        return variantsProjection.stream()
                .map(ProductMapper::toProductVariantDto)
                .collect(Collectors.toSet());
    }

    public static ProductVariantDto toProductVariantDto(ProductVariantProjection productVariant) {
        if (productVariant == null) {
            return null;
        }

        return new ProductVariantDto(
                productVariant.getId(),
                productVariant.getSku(),
                productVariant.getPrice().getAmount(),
                "",
                productVariant.getStock(),
                productVariant.getColor());
    }

    // product review
    public static List<ProductReviewDto> toProductReviewsDto(List<ProductReview> reviews) {
        if (reviews == null) {
            return Collections.emptyList();
        }
        return reviews.stream().map(ProductMapper::toReviewVariantDto).collect(Collectors.toList());
    }

    public static ProductReviewDto toReviewVariantDto(ProductReview productReview) {
        if (productReview == null) {
            return null;
        }

        return new ProductReviewDto(
                productReview.getId().id(),
                productReview.getCustomerId().id(),
                productReview.getRating().value(),
                productReview.getComment().text(),
                productReview.isVerified(),
                productReview.getHelpfulVotes(),
                productReview.getStatus());
    }

    public static List<ProductReviewDto> projectionToProductReviewsDto(List<ProductReviewProjection> reviews) {
        if (reviews == null) {
            return Collections.emptyList();
        }
        return reviews.stream().map(ProductMapper::toReviewVariantDto).collect(Collectors.toList());
    }

    public static ProductReviewDto toReviewVariantDto(ProductReviewProjection productReview) {
        if (productReview == null) {
            return null;
        }

        return new ProductReviewDto(
                productReview.getId(),
                productReview.getCustomerId(),
                productReview.getRating(),
                productReview.getComment(),
                productReview.isVerified(),
                productReview.getHelpfulVotes(),
                productReview.getStatus());
    }

    public static List<ProductReview> toProductReviews(List<ProductReviewDataModel> dataModels) {
        return dataModels.stream().map(ProductMapper::toProductReview).collect(Collectors.toList());
    }

    private static ProductReview toProductReview(ProductReviewDataModel dataModel) {
        ProductReviewId reviewId = new ProductReviewId(UUID.randomUUID()); // Assuming an ID is generated here
        CustomerId customerId = new CustomerId(dataModel.getCustomerId());
        Rating rating = new Rating(dataModel.getRating());
        Comment comment = new Comment(dataModel.getComment());

        ProductReview productReview = new ProductReview(reviewId, customerId, rating, comment);

        productReview.updateStatus(dataModel.getStatus());
        if (dataModel.isVerified()) {
            productReview.markAsVerified();
        }
        for (int i = 0; i < dataModel.getHelpfulVotes(); i++) {
            productReview.addHelpfulVote();
        }

        return productReview;
    }

    private static ProductReviewDataModel toProductReviewDataModel(
            ProductReview productReview, ProductDataModel productDataModel) {
        ProductReviewDataModel dataModel = new ProductReviewDataModel();
        dataModel.setProduct(productDataModel);
        dataModel.setId(productReview.getId().id());
        dataModel.setCustomerId(productReview.getCustomerId().id());
        dataModel.setRating(productReview.getRating().value());
        dataModel.setComment(productReview.getComment().text());
        dataModel.setStatus(productReview.getStatus());
        dataModel.setVerified(productReview.isVerified());
        dataModel.setHelpfulVotes(productReview.getHelpfulVotes());

        return dataModel;
    }

    public static List<ProductReviewDataModel> toProductReviewsDataModel(
            List<ProductReview> productReviews, ProductDataModel productDataModel) {
        if (productReviews == null) {
            return Collections.emptyList();
        }

        return productReviews.stream()
                .map(productReview -> toProductReviewDataModel(productReview, productDataModel))
                .collect(Collectors.toList());
    }

    // read models
    public static ProductReadModel toProductReadModel(ProductCreated productCreated) {
        if (productCreated == null) {
            return null;
        }
        ProductReadModel productReadModel = new ProductReadModel();
        productReadModel.setId(UlidCreator.getUlid().toUuid());
        productReadModel.setProductId(productCreated.productId().id());
        productReadModel.setCategoryId(productCreated.categoryId().id());
        if (productCreated.description() != null) {
            productReadModel.setDescription(productCreated.description().value());
        }
        productReadModel.setDimensions(new DimensionsVO(
                productCreated.dimensions().width(),
                productCreated.dimensions().height(),
                productCreated.dimensions().depth()));
        productReadModel.setName(productCreated.name().value());
        productReadModel.setPrice(new PriceVO(
                productCreated.price().amount(), productCreated.price().currency()));
        productReadModel.setSize(
                new SizeVO(productCreated.size().size(), productCreated.size().unit()));
        productReadModel.setStatus(productCreated.status());
        if (productCreated.reviews() != null) {
            productReadModel.setReviews(toProductReviewsReadModel(productCreated.reviews()));
        }
        if (productCreated.variants() != null) {
            productReadModel.setVariants(toProductVariantsReadModel(productCreated.variants()).stream()
                    .toList());
        }

        return productReadModel;
    }

    public static List<ProductReviewReadModel> toProductReviewsReadModel(List<ProductReview> reviews) {
        if (reviews == null) {
            return Collections.emptyList();
        }
        return reviews.stream().map(ProductMapper::toProductReviewReadModel).collect(Collectors.toList());
    }

    public static ProductReviewReadModel toProductReviewReadModel(ProductReview productReview) {
        if (productReview == null) {
            return null;
        }

        return new ProductReviewReadModel(
                UlidCreator.getUlid().toUuid(),
                productReview.getId().id(),
                productReview.getCustomerId().id(),
                productReview.getRating().value(),
                productReview.getComment().text(),
                productReview.isVerified(),
                productReview.getHelpfulVotes(),
                productReview.getStatus());
    }

    public static Set<ProductVariantReadModel> toProductVariantsReadModel(Set<ProductVariant> variants) {
        if (variants == null) {
            return Collections.emptySet();
        }
        return variants.stream().map(ProductMapper::toProductVariantReadModel).collect(Collectors.toSet());
    }

    public static ProductVariantReadModel toProductVariantReadModel(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        return new ProductVariantReadModel(
                UlidCreator.getUlid().toUuid(),
                productVariant.getId().id(),
                productVariant.getSku().value(),
                new MoneyVO(
                        productVariant.getPrice().amount(),
                        productVariant.getPrice().currency()),
                productVariant.getStock().quantity(),
                productVariant.getColor().value());
    }
}
