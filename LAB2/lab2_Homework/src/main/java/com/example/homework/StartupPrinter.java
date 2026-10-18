package com.example.homework;

import java.util.Arrays;
import com.example.homework.config.DataProperties;
import com.example.homework.data.ProductCatalog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupPrinter implements CommandLineRunner {

    private final ProductCatalog catalog;
    private final DataProperties properties;
    private final Environment environment;

    public StartupPrinter(ProductCatalog catalog, DataProperties properties, Environment environment) {
        this.catalog = catalog;
        this.properties = properties;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            profiles = environment.getDefaultProfiles();
        }
        System.out.printf("DATA profiles=%s location=%s format=%s reader=%s records=%d maxRecords=%d previewCount=%d%n",
                Arrays.toString(profiles), properties.location(), properties.format(),
                catalog.reader(), catalog.products().size(), properties.maxRecords(), properties.previewCount());
        catalog.products().stream().limit(properties.previewCount()).forEach(product ->
                System.out.printf("PRODUCT id=%d name=%s price=%s%n",
                        product.id(), product.name(), product.price()));
    }
}

