package ro.uaic.info.vros.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.entity.*;
import ro.uaic.info.vros.repository.*;
import ro.uaic.info.vros.service.UserAccountService;
import java.nio.charset.StandardCharsets;

@Component
@Order(0)
public class UserDataInitializer implements CommandLineRunner {
    private final CustomerRepository customers;
    private final DriverRepository drivers;
    private final AppUserRepository users;
    private final UserAccountService accounts;
    private final PasswordEncoder encoder;
    private final JdbcTemplate jdbc;
    private final String adminEmail;
    private final String adminPassword;

    public UserDataInitializer(CustomerRepository customers, DriverRepository drivers, AppUserRepository users,
                               UserAccountService accounts, PasswordEncoder encoder, JdbcTemplate jdbc,
                               @Value("${app.admin.email}") String adminEmail,
                               @Value("${app.admin.password}") String adminPassword) {
        this.customers = customers; this.drivers = drivers; this.users = users;
        this.accounts = accounts; this.encoder = encoder; this.jdbc = jdbc;
        this.adminEmail = adminEmail; this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Migreaza profilurile vechi fara a le sterge si fara parole implicite cunoscute.
        for (Customer customer : customers.findAll()) {
            if (customer.getUser() == null) accounts.connect(customer, Role.CUSTOMER);
        }
        for (Driver driver : drivers.findAll()) {
            if (driver.getUser() == null) accounts.connect(driver, Role.DRIVER);
        }
        users.flush(); customers.flush(); drivers.flush();
        jdbc.execute("ALTER TABLE customers ALTER COLUMN user_id SET NOT NULL");
        jdbc.execute("ALTER TABLE drivers ALTER COLUMN user_id SET NOT NULL");

        // Administratorul initial este creat doar daca parola este furnizata explicit.
        if (!adminPassword.isBlank()) {
            if (adminPassword.length() < 8 || adminPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("ADMIN_PASSWORD must have at least 8 characters and at most 72 bytes");
            }
            var existing = users.findByEmail(adminEmail);
            if (existing.isPresent() && !existing.get().getRoles().contains(Role.ADMIN)) {
                throw new IllegalStateException("ADMIN_EMAIL already belongs to a non-admin user");
            }
            if (existing.isEmpty()) {
                AppUser admin = new AppUser();
                admin.setEmail(adminEmail);
                admin.setPasswordHash(encoder.encode(adminPassword));
                admin.getRoles().add(Role.ADMIN);
                users.save(admin);
            }
        }
    }
}
