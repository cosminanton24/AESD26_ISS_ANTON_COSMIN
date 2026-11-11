package ro.uaic.info.vros.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.vros.dto.DeliveryRequest;
import ro.uaic.info.vros.dto.DeliveryResponse;
import ro.uaic.info.vros.service.DeliveryService;
import java.net.URI;
import java.util.List;

// Expune operatiile CRUD pentru livrari prin cereri HTTP si raspunsuri JSON.
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/api/deliveries", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryController {
    private final DeliveryService service;

    // Spring furnizeaza serviciul care verifica si salveaza livrarile.
    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    // GET /api/deliveries: returneaza toate livrarile sub forma de DTO-uri.
    @GetMapping
    public List<DeliveryResponse> findAll() {
        return service.getAll();
    }

    // GET dupa ID: returneaza livrarea sau 404 daca aceasta lipseste.
    @GetMapping("/{id}")
    public DeliveryResponse findById(@PathVariable Long id) {
        return service.getById(id);
    }

    // POST: valideaza datele, creeaza livrarea si returneaza 201 cu Location.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeliveryResponse> create(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/deliveries/" + saved.id())).body(saved);
    }

    // PUT: actualizeaza clientul, cantitatea si notitele; pastreaza data initiala.
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeliveryResponse update(@PathVariable Long id, @Valid @RequestBody DeliveryRequest request) {
        return service.update(id, request);
    }

    // DELETE: sterge livrarea si returneaza 204, fara corp de raspuns.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
