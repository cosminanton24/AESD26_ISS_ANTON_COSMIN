package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import ro.uaic.info.vros.entity.Role;
import ro.uaic.info.vros.entity.Customer;
import ro.uaic.info.vros.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository repository;
    private final UserAccountService accounts;

    public CustomerService(CustomerRepository repository, UserAccountService accounts) {
        this.repository = repository;
        this.accounts = accounts;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Customer save(Customer entity) {
        accounts.connect(entity, Role.CUSTOMER);
        return repository.save(entity);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
