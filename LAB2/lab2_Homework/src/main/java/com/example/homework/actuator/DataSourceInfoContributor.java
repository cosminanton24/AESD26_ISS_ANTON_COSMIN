package com.example.homework.actuator;

import java.util.List;
import java.util.Map;
import com.example.homework.config.DataProperties;
import com.example.homework.data.ProductCatalog;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DataSourceInfoContributor implements InfoContributor {

    private final DataProperties properties;
    private final ProductCatalog catalog;
    private final Environment environment;

    public DataSourceInfoContributor(DataProperties properties, ProductCatalog catalog, Environment environment) {
        this.properties = properties;
        this.catalog = catalog;
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            profiles = environment.getDefaultProfiles();
        }
        builder.withDetail("dataSource", Map.of(
                "location", properties.location(),
                "format", properties.format(),
                "recordsLoaded", catalog.products().size(),
                "reader", catalog.reader(),
                "profiles", List.of(profiles),
                "charset", properties.charset().name(),
                "maxRecords", properties.maxRecords(),
                "previewCount", properties.previewCount()));
    }
}

