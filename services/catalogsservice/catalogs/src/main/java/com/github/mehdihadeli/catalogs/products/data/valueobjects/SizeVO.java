package com.github.mehdihadeli.catalogs.products.data.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SizeVO {
    private String unit;
    private String size;

    // Default constructor for JPA
    public SizeVO() {}

    public SizeVO(String size, String unit) {
        this.size = size;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
