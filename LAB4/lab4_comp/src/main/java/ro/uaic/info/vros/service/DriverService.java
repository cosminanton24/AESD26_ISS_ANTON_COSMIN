package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import ro.uaic.info.vros.entity.Role;
import ro.uaic.info.vros.entity.Driver;
import ro.uaic.info.vros.repository.DriverRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DriverService {
    private final DriverRepository repository;
    private final UserAccountService accounts;

    public DriverService(DriverRepository repository, UserAccountService accounts) {
        this.repository = repository;
        this.accounts = accounts;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Driver save(Driver entity) {
        accounts.connect(entity, Role.DRIVER);
        return repository.save(entity);
    }

    public List<Driver> findAll() {
        return repository.findAll();
    }

    public Optional<Driver> findById(Long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
