package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import ro.uaic.info.vros.entity.Delivery;
import ro.uaic.info.vros.repository.DeliveryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {
    private final DeliveryRepository repository;

    public DeliveryService(DeliveryRepository repository) {
        this.repository = repository;
    }

    public Delivery save(Delivery entity) {
        return repository.save(entity);
    }

    public List<Delivery> findAll() {
        return repository.findAll();
    }

    public Optional<Delivery> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
