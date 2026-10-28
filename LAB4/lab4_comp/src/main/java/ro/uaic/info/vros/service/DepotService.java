package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import ro.uaic.info.vros.entity.Depot;
import ro.uaic.info.vros.repository.DepotRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DepotService {
    private final DepotRepository repository;

    public DepotService(DepotRepository repository) {
        this.repository = repository;
    }

    public Depot save(Depot entity) {
        return repository.save(entity);
    }

    public List<Depot> findAll() {
        return repository.findAll();
    }

    public Optional<Depot> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
