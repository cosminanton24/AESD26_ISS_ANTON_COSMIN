package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import ro.uaic.info.vros.entity.Vehicle;
import ro.uaic.info.vros.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public Vehicle save(Vehicle entity) {
        return repository.save(entity);
    }

    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Vehicle> findAvailableVehicles(Long depotId, int quantity) {
        return repository.findAvailableVehicles(depotId, quantity);
    }

    public int updateAvailability(Long id, boolean available) {
        return repository.updateAvailability(id, available);
    }
}
