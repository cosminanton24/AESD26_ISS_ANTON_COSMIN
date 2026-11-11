package ro.uaic.info.vros.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!dev")
public class SecurityConfig {
    // In prod, JWT-ul este verificat inainte de accesul la controllere.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService users) throws Exception {
        return http
                // Tokenul vine exclusiv in Authorization, fara Basic, cookie sau sesiune.
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        // Pastreaza statusurile 400/404 ale cererilor publice, fara a publica /error.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                        // Catalogul de vehicule/scenarii si documentatia sunt publice.
                        .requestMatchers(HttpMethod.GET, "/api/vehicles", "/api/vehicles/**",
                                "/api/scenarios", "/api/scenarios/**", "/swagger-ui.html",
                                "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/deliveries").hasAnyRole("ADMIN", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(token -> {
                    // Reincarca rolurile din DB; modificarile de rol se aplica si unui token deja emis.
                    var user = users.loadUserByUsername(token.getSubject());
                    return new JwtAuthenticationToken(token, user.getAuthorities(), user.getUsername());
                })))
                .build();
    }
}
