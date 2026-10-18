package com.example.homework.model;

import java.math.BigDecimal;

public record Product(long id, String name, BigDecimal price) {

    public Product {
        if (id <= 0 || name == null || name.isBlank() || price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product requires a positive id, a name and a non-negative price");
        }
    }
}

