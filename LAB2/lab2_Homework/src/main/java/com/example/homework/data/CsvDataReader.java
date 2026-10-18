package com.example.homework.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.example.homework.config.DataProperties;
import com.example.homework.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Profile("dev | prod")
@ConditionalOnExpression("'${app.data.format}' == 'csv'")
public class CsvDataReader implements DataReader {

    private final DataProperties properties;
    private final ResourceLoader resourceLoader;

    public CsvDataReader(DataProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<Product> read() throws IOException {
        var format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setDelimiter(properties.delimiter().charAt(0))
                .setTrim(true)
                .get();
        var products = new ArrayList<Product>();
        try (var reader = new InputStreamReader(
                resourceLoader.getResource(properties.location()).getInputStream(), properties.charset());
             var parser = format.parse(reader)) {
            if (!parser.getHeaderNames().equals(List.of("id", "name", "price"))) {
                throw new IOException("CSV header must be: id,name,price");
            }
            for (var row : parser) {
                products.add(new Product(Long.parseLong(row.get("id")),
                        row.get("name"), new BigDecimal(row.get("price"))));
                if (products.size() == properties.maxRecords()) {
                    break;
                }
            }
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid CSV data in " + properties.location(), exception);
        }
        return List.copyOf(products);
    }
}

