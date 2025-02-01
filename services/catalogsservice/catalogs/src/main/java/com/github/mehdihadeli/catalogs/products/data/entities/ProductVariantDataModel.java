package com.github.mehdihadeli.catalogs.products.data.entities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableEntityDataModel;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.MoneyVO;
import jakarta.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariantDataModel extends AuditableEntityDataModel {

    // https://www.callicoder.com/hibernate-spring-boot-jpa-one-to-many-mapping-example/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductDataModel product;

    @Column(nullable = false, unique = true)
    private String sku;

    // - By default, JPA does not prepend the embedded field’s name (e.g., price_ for PriceVO). Instead, it directly
    // uses the field names inside PriceVO, leading to amount and currency instead of price_amount and price_currency.
    // The @AttributeOverrides annotation explicitly tells JPA how to map these fields.
    @AttributeOverrides(
            value = {
                @AttributeOverride(name = "amount", column = @Column(name = "money_amount")),
                @AttributeOverride(name = "currency", column = @Column(name = "money_currency"))
            })
    @Column(nullable = false)
    @Embedded
    private MoneyVO price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String color;

    // Default constructor for JPA
    public ProductVariantDataModel() {}

    public ProductDataModel getProduct() {
        return product;
    }

    public void setProduct(ProductDataModel product) {
        this.product = product;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public MoneyVO getPrice() {
        return price;
    }

    public void setPrice(MoneyVO price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}