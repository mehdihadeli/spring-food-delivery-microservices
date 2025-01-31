package com.github.mehdihadeli.catalogs.products.data.entities;

import com.github.mehdihadeli.catalogs.products.data.valueobjects.MoneyVO;
import com.github.mehdihadeli.buildingblocks.core.data.AuditableEntityDataModel;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "product_variants")
public class ProductVariantDataModel extends AuditableEntityDataModel {

    // https://www.callicoder.com/hibernate-spring-boot-jpa-one-to-many-mapping-example/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductDataModel product;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    @Embedded
    private MoneyVO price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String color;

    // https://www.callicoder.com/hibernate-spring-boot-jpa-element-collection-demo/
    @ElementCollection
    @CollectionTable(name = "product_variant_attributes", joinColumns = @JoinColumn(name = "variant_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();

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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}