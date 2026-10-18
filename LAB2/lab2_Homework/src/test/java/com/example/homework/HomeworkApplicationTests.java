package com.example.homework;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import com.example.homework.actuator.DataSourceInfoContributor;
import com.example.homework.config.DataProperties;
import com.example.homework.data.DataReader;
import com.example.homework.data.ProductCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.info.Info;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import static org.junit.jupiter.api.Assertions.*;

class HomeworkApplicationTests {

    @TempDir
    Path directory;

    private ConfigurableApplicationContext start(Map<String, Object> variables, String... arguments) {
        var environment = new StandardEnvironment();
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, variables));
        var application = new SpringApplication(HomeworkApplication.class);
        application.setEnvironment(environment);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setLogStartupInfo(false);
        var options = new ArrayList<>(Arrays.asList(arguments));
        options.add("--spring.main.banner-mode=off");
        options.add("--logging.level.root=OFF");
        return application.run(options.toArray(String[]::new));
    }

    @Test
    void defaultProfileLoadsDevelopmentCsvAndQuotedFields() {
        try (var context = start(Map.of())) {
            var catalog = context.getBean(ProductCatalog.class);
            assertEquals("CsvDataReader", catalog.reader());
            assertEquals(4, catalog.products().size());
            assertEquals("Pencil, blue", catalog.products().get(2).name());
            assertEquals(1, context.getBeansOfType(DataReader.class).size());
        }
    }

    @Test
    void productionLoadsJsonAndReportsActualRecordCount() {
        try (var context = start(Map.of(), "--spring.profiles.active=prod")) {
            var catalog = context.getBean(ProductCatalog.class);
            assertEquals("JsonDataReader", catalog.reader());
            assertEquals(5, catalog.products().size());
            assertEquals("Laptop", catalog.products().get(0).name());
            var builder = new Info.Builder();
            context.getBean(DataSourceInfoContributor.class).contribute(builder);
            var source = (Map<?, ?>) builder.build().getDetails().get("dataSource");
            assertEquals("classpath:data/products-prod.json", source.get("location"));
            assertEquals("json", source.get("format"));
            assertEquals(5, source.get("recordsLoaded"));
        }
    }

    @Test
    void basePropertiesApplyWithoutProfileFile() throws Exception {
        try (var source = getClass().getResourceAsStream("/application.properties")) {
            Files.copy(source, directory.resolve("application.properties"));
        }
        try (var context = start(Map.of(), "--spring.profiles.active=dev",
                "--spring.config.location=" + directory.toUri())) {
            assertEquals(1, context.getBean(DataProperties.class).previewCount());
            assertEquals("classpath:data/products-default.csv", context.getBean(DataProperties.class).location());
            assertEquals(1, context.getBean(ProductCatalog.class).products().size());
        }
    }

    @Test
    void profileYamlOverridesBaseProperties() {
        try (var context = start(Map.of(), "--spring.profiles.active=dev")) {
            assertEquals(3, context.getBean(DataProperties.class).previewCount());
            assertEquals("classpath:data/products-dev.csv", context.getBean(DataProperties.class).location());
            assertEquals("UTF-8", context.getBean(DataProperties.class).charset().name());
        }
    }

    @Test
    void environmentOverridesProfileAndBaseProperties() {
        try (var context = start(Map.of("APP_DATA_PREVIEWCOUNT", "4",
                "APP_DATA_LOCATION", "classpath:data/products-default.csv"), "--spring.profiles.active=dev")) {
            assertEquals(4, context.getBean(DataProperties.class).previewCount());
            assertEquals("classpath:data/products-default.csv", context.getBean(DataProperties.class).location());
        }
    }

    @Test
    void commandLineOverridesEnvironmentProfileAndBaseProperties() {
        try (var context = start(Map.of("APP_DATA_PREVIEWCOUNT", "4",
                "APP_DATA_LOCATION", "classpath:data/products-default.csv"), "--spring.profiles.active=dev",
                "--app.data.preview-count=5", "--app.data.location=classpath:data/products-dev.csv")) {
            assertEquals(5, context.getBean(DataProperties.class).previewCount());
            assertEquals("classpath:data/products-dev.csv", context.getBean(DataProperties.class).location());
        }
    }

    @Test
    void productionCanSelectCsvWithoutCodeChanges() {
        try (var context = start(Map.of(), "--spring.profiles.active=prod",
                "--app.data.format=csv", "--app.data.location=classpath:data/products-dev.csv")) {
            assertEquals("CsvDataReader", context.getBean(ProductCatalog.class).reader());
            assertEquals(1, context.getBeansOfType(DataReader.class).size());
        }
    }

    @Test
    void developmentCanSelectJsonAndLimitRecords() {
        try (var context = start(Map.of(), "--spring.profiles.active=dev", "--app.data.format=json",
                "--app.data.location=classpath:data/products-prod.json", "--app.data.max-records=2")) {
            assertEquals("JsonDataReader", context.getBean(ProductCatalog.class).reader());
            assertEquals(2, context.getBean(ProductCatalog.class).products().size());
        }
    }

    @Test
    void externalCsvRespectsDelimiterAndRecordLimit() throws Exception {
        var csv = directory.resolve("products.csv");
        Files.writeString(csv, "id;name;price\n1;External pen;4.25\n2;External notebook;8.50\n");
        try (var context = start(Map.of(), "--app.data.location=" + csv.toUri(),
                "--app.data.delimiter=;", "--app.data.max-records=1")) {
            var products = context.getBean(ProductCatalog.class).products();
            assertEquals(1, products.size());
            assertEquals("External pen", products.get(0).name());
        }
    }

    @Test
    void invalidRecordLimitPreventsStartup() {
        assertThrows(Exception.class, () -> {
            try (var ignored = start(Map.of(), "--app.data.max-records=0")) {
                fail("Invalid configuration should prevent startup");
            }
        });
    }

    @Test
    void missingFilePreventsStartup() {
        assertThrows(Exception.class, () -> {
            try (var ignored = start(Map.of(), "--app.data.location=classpath:data/missing.csv")) {
                fail("A missing data file should prevent startup");
            }
        });
    }

    @Test
    void unsupportedProfileDoesNotCreateReader() {
        assertThrows(Exception.class, () -> {
            try (var ignored = start(Map.of(), "--spring.profiles.active=unsupported")) {
                fail("An unsupported profile should not create a reader");
            }
        });
    }
}

