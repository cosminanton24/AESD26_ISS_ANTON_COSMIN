package com.example.homework.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import com.example.homework.config.DataProperties;
import com.example.homework.model.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component
@Profile("dev | prod")
@ConditionalOnExpression("'${app.data.format}' == 'json'")
public class JsonDataReader implements DataReader {

    private final DataProperties properties;
    private final ResourceLoader resourceLoader;
    private final JsonMapper mapper;

    public JsonDataReader(DataProperties properties, ResourceLoader resourceLoader, JsonMapper mapper) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.mapper = mapper;
    }

    @Override
    public List<Product> read() throws IOException {
        try (var reader = new InputStreamReader(
                resourceLoader.getResource(properties.location()).getInputStream(), properties.charset())) {
            List<Product> products = mapper.readValue(reader, new TypeReference<List<Product>>() {});
            if (products == null || products.stream().anyMatch(java.util.Objects::isNull)) {
                throw new IOException("JSON must contain an array of products");
            }
            return List.copyOf(products.subList(0, Math.min(products.size(), properties.maxRecords())));
        }
    }
}

