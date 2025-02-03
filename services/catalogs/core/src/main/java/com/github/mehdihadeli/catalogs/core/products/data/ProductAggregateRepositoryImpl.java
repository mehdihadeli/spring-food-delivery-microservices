package com.github.mehdihadeli.catalogs.core.products.data;

import static com.github.mehdihadeli.catalogs.core.products.data.entities.QProductDataModel.productDataModel;
import static com.github.mehdihadeli.catalogs.core.products.data.entities.QProductReviewDataModel.productReviewDataModel;
import static com.github.mehdihadeli.catalogs.core.products.data.entities.QProductVariantDataModel.productVariantDataModel;

import com.github.mehdihadeli.buildingblocks.core.data.AggregateBaseRepository;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductJpaRepository;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.projections.ProductSummaryProjection;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.Product;
import com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects.ProductId;
import com.github.mehdihadeli.catalogs.core.products.dtos.ProductSummaryDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

// The `@Repository` annotation needs to be on the concrete class not interface and after that interface is accessible
// on dependency injection
@Repository
public class ProductAggregateRepositoryImpl extends AggregateBaseRepository<Product, ProductDataModel, ProductId, UUID>
        implements ProductAggregateRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;
    ProductJpaRepository productJpaRepository;

    protected ProductAggregateRepositoryImpl(EntityManager entityManager, ProductJpaRepository productJpaRepository) {
        super(entityManager, productJpaRepository);
        this.entityManager = entityManager;
        this.productJpaRepository = productJpaRepository;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ProductSummaryProjection> findByPage(Pageable pageable) {
        return productJpaRepository.findProductSummaryProjectionBy(pageable);
    }

    @Override
    public Page<ProductSummaryDTO> searchByName(Pageable pageable, String name) {
        // https://www.baeldung.com/intro-to-querydsl
        // https://www.baeldung.com/jpa-criteria-querydsl-differences
        var results = jpaQueryFactory
                .select(Projections.constructor(
                        ProductSummaryDTO.class,
                        productDataModel.id,
                        productDataModel.categoryId,
                        productDataModel.name,
                        Projections.constructor(ProductSummaryDTO.PriceDTO.class, productDataModel.price.amount),
                        Projections.constructor(ProductSummaryDTO.SizeDTO.class, productDataModel.size.size),
                        productDataModel.status,
                        Projections.list(Projections.constructor(
                                ProductSummaryDTO.ProductVariantDTO.class,
                                productVariantDataModel.id,
                                productVariantDataModel.product.id,
                                productVariantDataModel.sku,
                                productVariantDataModel.color,
                                productVariantDataModel.stock)),
                        Projections.list(Projections.constructor(
                                ProductSummaryDTO.ProductReviewDTO.class,
                                productReviewDataModel.id,
                                productReviewDataModel.product.id,
                                productReviewDataModel.customerId,
                                productReviewDataModel.rating,
                                productReviewDataModel.comment))))
                .from(productDataModel)
                .leftJoin(productDataModel.variants, productVariantDataModel)
                .on(productDataModel.id.eq(productVariantDataModel.product.id))
                .leftJoin(productDataModel.reviews, productReviewDataModel)
                .on(productDataModel.id.eq(productReviewDataModel.product.id))
                .where(productDataModel.name.containsIgnoreCase(name))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(productDataModel.count())
                .from(productDataModel)
                .where(productDataModel.name.containsIgnoreCase(name))
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    //    @Override
    //    public Page<ProductSummaryDTO> searchByName(Pageable pageable, String name) {
    //        // https://www.baeldung.com/hibernate-criteria-queries
    //        // https://www.baeldung.com/jpa-hibernate-projections
    //        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    //        CriteriaQuery<ProductSummaryDTO> criteriaQuery = criteriaBuilder.createQuery(ProductSummaryDTO.class);
    //
    //        // Define the root for the query
    //        Root<ProductDataModel> productDataModel = criteriaQuery.from(ProductDataModel.class);
    //        Join<ProductDataModel, ProductVariantDataModel> productVariants =
    //                productDataModel.join("variants", JoinType.LEFT);
    //        Join<ProductDataModel, ProductReviewDataModel> productReviews = productDataModel.join("reviews",
    // JoinType.LEFT);
    //
    //        // Define the selection (projection)
    //        criteriaQuery.select(criteriaBuilder.construct(
    //                ProductSummaryDTO.class,
    //                productDataModel.get(ProductDataModel_.id),
    //                productDataModel.get(ProductDataModel_.categoryId),
    //                productDataModel.get(ProductDataModel_.name),
    //                criteriaBuilder.construct(
    //                        ProductSummaryDTO.PriceDTO.class,
    //                        productDataModel.get(ProductDataModel_.price).get(PriceVO_.amount)),
    //                criteriaBuilder.construct(
    //                        ProductSummaryDTO.SizeDTO.class,
    //                        productDataModel.get(ProductDataModel_.size).get(SizeVO_.size)),
    //                productDataModel.get(ProductDataModel_.status),
    //                // Handle variants as a subquery or collection
    //                criteriaBuilder.construct(
    //                        ProductSummaryDTO.ProductVariantDTO.class,
    //                        productVariants.get(ProductVariantDataModel_.id),
    //                        productVariants.get(ProductVariantDataModel_.product).get(ProductDataModel_.id),
    //                        productVariants.get(ProductVariantDataModel_.sku),
    //                        productVariants.get(ProductVariantDataModel_.color),
    //                        productVariants.get(ProductVariantDataModel_.stock)),
    //                // Handle reviews similarly
    //                criteriaBuilder.construct(
    //                        ProductSummaryDTO.ProductReviewDTO.class,
    //                        productReviews.get(ProductReviewDataModel_.id),
    //                        productReviews.get(ProductReviewDataModel_.product).get(ProductDataModel_.id),
    //                        productReviews.get(ProductReviewDataModel_.customerId),
    //                        productReviews.get(ProductReviewDataModel_.rating),
    //                        productReviews.get(ProductReviewDataModel_.comment))));
    //
    //        // Add where clause
    //        criteriaQuery.where(criteriaBuilder.like(
    //                criteriaBuilder.lower(productDataModel.get(ProductDataModel_.name)), "%" + name.toLowerCase() +
    // "%"));
    //
    //        // Create TypedQuery and set pagination
    //        TypedQuery<ProductSummaryDTO> typedQuery = entityManager.createQuery(criteriaQuery);
    //        typedQuery.setFirstResult((int) pageable.getOffset());
    //        typedQuery.setMaxResults(pageable.getPageSize());
    //
    //        // Fetch results
    //        List<ProductSummaryDTO> results = typedQuery.getResultList();
    //
    //        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    //        countQuery
    //                .select(criteriaBuilder.count(countQuery.from(ProductDataModel.class)))
    //                .where(criteriaBuilder.like(
    //                        criteriaBuilder.lower(productDataModel.get(ProductDataModel_.name)),
    //                        "%" + name.toLowerCase() + "%"));
    //        Long total = entityManager.createQuery(countQuery).getSingleResult();
    //
    //        return new PageImpl<>(results, pageable, total);
    //    }

    @Override
    public Product toAggregate(ProductDataModel productDataModel) {
        return ProductMapper.toProductAggregate(productDataModel);
    }

    @Override
    public ProductDataModel toDataModel(Product product) {
        return ProductMapper.toProductDataModel(product);
    }

    @Override
    public ProductDataModel toDataModel(Product product, ProductDataModel productDataModel) {
        return ProductMapper.toProductDataModel(product, productDataModel);
    }
}
