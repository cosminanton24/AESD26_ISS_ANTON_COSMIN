package ro.uaic.info.vros.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ro.uaic.info.vros.repository.CustomerRepository;

@Component("accessPolicy")
public class AccessPolicy {
    private final CustomerRepository customers;
    public AccessPolicy(CustomerRepository customers) { this.customers = customers; }

    // Clientul poate crea o livrare doar pentru propriul profil, nu pentru alt ID.
    public boolean isCustomer(Long customerId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && customers.existsByIdAndUserEmail(customerId, auth.getName());
    }
}
