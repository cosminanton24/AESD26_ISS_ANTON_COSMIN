package com.example.homework.data;

import java.io.IOException;
import java.util.List;
import com.example.homework.config.DataProperties;
import com.example.homework.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalog {

    private final List<Product> products;
    private final String reader;

    public ProductCatalog(DataReader dataReader, DataProperties properties) throws IOException {
        try {
            products = dataReader.read();
            reader = dataReader.getClass().getSimpleName();
        } catch (IOException | RuntimeException exception) {
            throw new IOException("Cannot load products from " + properties.location(), exception);
        }
    }

    public List<Product> products() {
        return products;
    }

    public String reader() {
        return reader;
    }
}

