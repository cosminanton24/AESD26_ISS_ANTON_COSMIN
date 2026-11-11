package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.dto.VehicleRequest;
import ro.uaic.info.vros.dto.VehicleResponse;
import ro.uaic.info.vros.exception.ResourceNotFoundException;
import ro.uaic.info.vros.repository.DepotRepository;
import ro.uaic.info.vros.entity.Vehicle;
import ro.uaic.info.vros.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {
    private final VehicleRepository repository;
    private final DepotRepository depots;

    public VehicleService(VehicleRepository repository, DepotRepository depots) {
        this.repository = repository;
        this.depots = depots;
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public Vehicle save(Vehicle entity) {
        return repository.save(entity);
    }

    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id);
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repository.delete(requireVehicle(id));
    }

    public List<VehicleResponse> getAll() {
        return repository.findAll(Sort.by("id")).stream().map(VehicleResponse::from).toList();
    }

    public VehicleResponse getById(Long id) {
        return VehicleResponse.from(requireVehicle(id));
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setDepot(depots.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Depot", 1L)));
        applyRequest(vehicle, request);
        return VehicleResponse.from(repository.save(vehicle));
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = requireVehicle(id);
        applyRequest(vehicle, request);
        return VehicleResponse.from(repository.save(vehicle));
    }

    private Vehicle requireVehicle(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    private void applyRequest(Vehicle vehicle, VehicleRequest request) {
        vehicle.setName(request.name());
        vehicle.setCapacity(request.capacity());
        vehicle.setAvailable(request.available());
        vehicle.setDriver(null);
    }

    public List<Vehicle> findAvailableVehicles(Long depotId, int quantity) {
        return repository.findAvailableVehicles(depotId, quantity);
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public int updateAvailability(Long id, boolean available) {
        return repository.updateAvailability(id, available);
    }
}
