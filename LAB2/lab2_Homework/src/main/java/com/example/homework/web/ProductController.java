package com.example.homework.web;

import java.util.List;
import com.example.homework.data.ProductCatalog;
import com.example.homework.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductCatalog catalog;

    public ProductController(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    public List<Product> products() {
        return catalog.products();
    }
}

