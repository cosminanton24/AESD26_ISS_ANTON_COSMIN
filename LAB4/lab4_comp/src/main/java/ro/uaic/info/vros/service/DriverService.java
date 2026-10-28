package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import ro.uaic.info.vros.entity.Driver;
import ro.uaic.info.vros.repository.DriverRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository repository;

    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    public Driver save(Driver entity) {
        return repository.save(entity);
    }

    public List<Driver> findAll() {
        return repository.findAll();
    }

    public Optional<Driver> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
