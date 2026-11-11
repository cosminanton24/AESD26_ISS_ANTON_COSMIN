package ro.uaic.info.vros.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// In dev nu se activeaza interceptorul care aplica @PreAuthorize.
@Configuration
@Profile("!dev")
@EnableMethodSecurity
public class MethodSecurityConfig {
}
