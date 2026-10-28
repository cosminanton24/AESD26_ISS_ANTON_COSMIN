package ro.uaic.info.vros.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.uaic.info.vros.dto.CustomerRequest;
import ro.uaic.info.vros.dto.CustomerResponse;
import ro.uaic.info.vros.entity.Customer;
import ro.uaic.info.vros.service.CustomerService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // READ: lista tuturor clientilor.
    @GetMapping
    public List<CustomerResponse> findAll() {
        return service.findAll().stream().map(CustomerResponse::from).toList();
    }

    // READ: un client identificat prin ID.
    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return CustomerResponse.from(requireCustomer(id));
    }

    // CREATE: adauga un client si returneaza 201 Created.
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        Customer saved = service.save(customer);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getId()))
                .body(CustomerResponse.from(saved));
    }

    // UPDATE: inlocuieste datele unui client existent.
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CustomerRequest request) {
        Customer customer = requireCustomer(id);
        applyRequest(customer, request);
        return CustomerResponse.from(service.save(customer));
    }

    // DELETE: sterge clientul si returneaza 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Customer customer = requireCustomer(id);
        service.deleteById(customer.getId());
        return ResponseEntity.noContent().build();
    }

    // Email duplicat sau stergerea unui client cu livrari existente.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Email already used or customer referenced by deliveries."));
    }

    private Customer requireCustomer(Long id) {
        return service.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setCoordinates(request.coordinates());
    }
}
