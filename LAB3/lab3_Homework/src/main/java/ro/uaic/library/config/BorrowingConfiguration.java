package ro.uaic.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.library.policy.*;

@Configuration
public class BorrowingConfiguration {
    @Bean
    public BorrowingPolicy borrowingPolicy(BorrowingProperties properties) {
        return switch (properties.policy()) {
            case FIXED -> new FixedBorrowingPolicy(properties.fixedLimit());
            case ROLE_BASED -> new RoleBasedBorrowingPolicy(properties.studentLimit(),
                    properties.regularLimit(), properties.premiumLimit());
        };
    }
}
