package ro.uaic.info.vros.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.dto.ScenarioRequest;
import ro.uaic.info.vros.dto.ScenarioResponse;
import ro.uaic.info.vros.entity.Scenario;
import ro.uaic.info.vros.entity.Vehicle;
import ro.uaic.info.vros.entity.Delivery;
import ro.uaic.info.vros.exception.ResourceNotFoundException;
import ro.uaic.info.vros.repository.*;
import java.util.HashSet;
import java.util.List;

// Gestioneaza scenariile si legaturile lor cu vehiculele si livrarile.
// Tranzactia pastreaza operatiile impreuna si permite citirea relatiilor JPA.
@Service
@Transactional
public class ScenarioService {
    private final ScenarioRepository repository;
    private final VehicleRepository vehicles;
    private final DeliveryRepository deliveries;

    // Spring furnizeaza repository-urile necesare prin constructor.
    public ScenarioService(ScenarioRepository repository, VehicleRepository vehicles,
                           DeliveryRepository deliveries) {
        this.repository = repository;
        this.vehicles = vehicles;
        this.deliveries = deliveries;
    }

    // Listeaza scenariile in ordinea ID-ului si le transforma in DTO-uri.
    public List<ScenarioResponse> getAll() {
        return repository.findAll(Sort.by("id")).stream().map(ScenarioResponse::from).toList();
    }

    // Citeste un scenariu; cautarea arunca exceptia proprie daca acesta lipseste.
    public ScenarioResponse getById(Long id) {
        return ScenarioResponse.from(requireScenario(id));
    }

    // Creeaza scenariul folosind numele si resursele indicate in cerere.
    public ScenarioResponse create(ScenarioRequest request) {
        Scenario scenario = new Scenario();
        applyRequest(scenario, request);
        return ScenarioResponse.from(repository.save(scenario));
    }

    // Inlocuieste numele si selectia de vehicule/livrari ale scenariului existent.
    public ScenarioResponse update(Long id, ScenarioRequest request) {
        Scenario scenario = requireScenario(id);
        applyRequest(scenario, request);
        return ScenarioResponse.from(repository.save(scenario));
    }

    // Sterge scenariul si asocierile sale, pastrand vehiculele si livrarile.
    public void deleteById(Long id) {
        repository.delete(requireScenario(id));
    }

    // Centralizeaza cautarea dupa ID si cazul in care scenariul nu exista.
    private Scenario requireScenario(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Scenario", id));
    }

    // Transforma ID-urile din DTO in entitati care pot fi asociate scenariului.
    private void applyRequest(Scenario scenario, ScenarioRequest request) {
        scenario.setName(request.name());
        // Fiecare vehicul trebuie sa existe; Set evita selectiile duplicate.
        var selectedVehicles = new HashSet<Vehicle>();
        for (Long id : request.vehicleIds()) {
            selectedVehicles.add(vehicles.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id)));
        }
        // Verifica in acelasi mod existenta livrarilor selectate.
        var selectedDeliveries = new HashSet<Delivery>();
        for (Long id : request.deliveryIds()) {
            selectedDeliveries.add(deliveries.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery", id)));
        }
        // JPA va salva legaturile in cele doua tabele intermediare.
        scenario.setVehicles(selectedVehicles);
        scenario.setDeliveries(selectedDeliveries);
    }
}
