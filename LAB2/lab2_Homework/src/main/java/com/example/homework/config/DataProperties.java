package com.example.homework.config;

import java.nio.charset.Charset;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * ConfigurationProperties groups related file options, binds typed values and validates
 * them together. Separate Value fields would scatter this configuration across the reader.
 */
@Validated
@ConfigurationProperties(prefix = "app.data")
public record DataProperties(
        @NotBlank String location,
        @NotBlank @Pattern(regexp = "csv|json") String format,
        @NotNull Charset charset,
        @NotNull @Size(min = 1, max = 1) String delimiter,
        @Min(1) int maxRecords,
        @Min(1) int previewCount) {
}

