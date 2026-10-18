package ro.uaic.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import ro.uaic.library.config.*;
import ro.uaic.library.domain.*;
import ro.uaic.library.policy.*;
import static org.assertj.core.api.Assertions.*;

class ConfigurationTest {
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(BorrowingProperties.class)
    static class PropertiesConfiguration { }
    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(PropertiesConfiguration.class, BorrowingConfiguration.class);
    @Test void missingPolicyUsesFixedDefault() {
        context.run(c -> {
            assertThat(c).hasSingleBean(BorrowingPolicy.class);
            assertThat(c.getBean(BorrowingPolicy.class)).isInstanceOf(FixedBorrowingPolicy.class);
        });
    }
    @Test void customFixedLimitIsUsed() {
        context.withPropertyValues("library.borrowing.fixed-limit=7").run(c ->
                assertThat(c.getBean(BorrowingPolicy.class).limitFor(new Member(1, "A", MemberRole.STUDENT))).isEqualTo(7));
    }
    @Test void invalidPolicyFailsStartup() {
        context.withPropertyValues("library.borrowing.policy=typo").run(c -> assertThat(c).hasFailed());
    }
    @Test void invalidLimitsAndThresholdFailStartup() {
        for (String property : new String[]{"fixed-limit=0", "student-limit=0", "regular-limit=0", "premium-limit=0", "event-threshold=-1"}) {
            context.withPropertyValues("library.borrowing." + property).run(c -> assertThat(c).hasFailed());
        }
    }
}
