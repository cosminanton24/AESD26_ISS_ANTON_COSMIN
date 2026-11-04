package ro.uaic.info.vros.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // Spring aplica aceste reguli inainte ca cererile sa ajunga la controllere.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // API-ul foloseste HTTP Basic fara formular sau sesiune de autentificare.
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        // Singura adresa accesibila fara autentificare.
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                // Verifica utilizatorul si parola transmise in antetul Authorization.
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
