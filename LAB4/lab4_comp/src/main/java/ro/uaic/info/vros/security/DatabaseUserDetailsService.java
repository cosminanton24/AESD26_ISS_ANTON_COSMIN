package ro.uaic.info.vros.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ro.uaic.info.vros.repository.AppUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;

    public DatabaseUserDetailsService(AppUserRepository users) { this.users = users; }

    // Spring foloseste hash-ul BCrypt si rolurile din PostgreSQL pentru autentificare.
    @Override
    public UserDetails loadUserByUsername(String email) {
        var user = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash())
                .authorities(user.getRoles().stream().map(role -> "ROLE_" + role.name()).toArray(String[]::new))
                .build();
    }
}
