package ro.uaic.library.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("library.borrowing")
public record BorrowingProperties(
        @NotNull @DefaultValue("fixed") Policy policy,
        @Min(1) @DefaultValue("3") int fixedLimit,
        @Min(1) @DefaultValue("2") int studentLimit,
        @Min(1) @DefaultValue("3") int regularLimit,
        @Min(1) @DefaultValue("5") int premiumLimit,
        @Min(0) @DefaultValue("2") int eventThreshold) {
    public enum Policy { FIXED, ROLE_BASED }
}
