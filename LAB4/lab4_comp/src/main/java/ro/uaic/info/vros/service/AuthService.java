package ro.uaic.info.vros.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ro.uaic.info.vros.dto.*;
import ro.uaic.info.vros.entity.*;
import ro.uaic.info.vros.repository.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;

@Service
public class AuthService {
    private final AppUserRepository users;
    private final CustomerRepository customers;
    private final DriverRepository drivers;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authentication;
    private final JwtEncoder tokens;
    private final long ttl;

    public AuthService(AppUserRepository users, CustomerRepository customers, DriverRepository drivers,
                       PasswordEncoder encoder, AuthenticationManager authentication, JwtEncoder tokens,
                       @Value("${app.jwt.ttl-seconds}") long ttl) {
        this.users = users; this.customers = customers; this.drivers = drivers;
        this.encoder = encoder; this.authentication = authentication; this.tokens = tokens; this.ttl = ttl;
    }

    // AuthenticationManager compara parola primita cu hash-ul BCrypt din DB.
    public TokenResponse login(LoginRequest request) {
        var auth = authentication.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("vros").subject(auth.getName())
                .issuedAt(now).expiresAt(now.plusSeconds(ttl))
                .claim("roles", auth.getAuthorities().stream()
                        .map(a -> a.getAuthority()).filter(authority -> authority.startsWith("ROLE_"))
                        .map(authority -> authority.substring(5)).toList())
                .build();
        String token = tokens.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", ttl);
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Inregistrarea publica nu poate acorda privilegii de administrator.
        if (request.role() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Register as CUSTOMER or DRIVER only");
        }
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password exceeds BCrypt's 72-byte limit");
        }
        if (request.role() == Role.CUSTOMER && (request.address() == null || request.address().isBlank()
                || request.coordinates() == null || request.coordinates().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customers require address and coordinates");
        }
        if (users.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPasswordHash(encoder.encode(request.password()));
        user.getRoles().add(request.role());
        users.save(user);

        Long customerId = null;
        Long driverId = null;
        Person profile;
        if (request.role() == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setAddress(request.address());
            customer.setCoordinates(request.coordinates());
            profile = customer;
        } else {
            profile = new Driver();
        }
        profile.setName(request.name()); profile.setEmail(request.email());
        profile.setPhone(request.phone()); profile.setUser(user);
        if (profile instanceof Customer customer) customerId = customers.save(customer).getId();
        if (profile instanceof Driver driver) driverId = drivers.save(driver).getId();
        return new UserResponse(user.getId(), user.getEmail(), Set.copyOf(user.getRoles()), customerId, driverId);
    }
}
