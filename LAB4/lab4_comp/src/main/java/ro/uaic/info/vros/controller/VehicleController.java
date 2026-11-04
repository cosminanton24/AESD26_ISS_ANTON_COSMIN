package ro.uaic.info.vros.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.vros.dto.VehicleRequest;
import ro.uaic.info.vros.dto.VehicleResponse;
import ro.uaic.info.vros.service.VehicleService;
import java.net.URI;
import java.util.List;

// Primeste cereri HTTP pentru vehicule si returneaza DTO-uri in format JSON.
@RestController
@RequestMapping(value = "/api/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleController {
    private final VehicleService service;

    // Spring injecteaza serviciul care gestioneaza accesul la date.
    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // GET /api/vehicles: returneaza lista tuturor vehiculelor.
    @GetMapping
    public List<VehicleResponse> findAll() {
        return service.getAll();
    }

    // GET dupa ID: returneaza vehiculul sau 404 daca acesta nu exista.
    @GetMapping("/{id}")
    public VehicleResponse findById(@PathVariable Long id) {
        return service.getById(id);
    }

    // POST: valideaza datele si returneaza vehiculul creat, status 201 si Location.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/vehicles/" + saved.id())).body(saved);
    }

    // PUT: inlocuieste datele vehiculului existent cu cele din cererea validata.
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return service.update(id, request);
    }

    // DELETE: sterge vehiculul si raspunde cu 204, fara continut.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
