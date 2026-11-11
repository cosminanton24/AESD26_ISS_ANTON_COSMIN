package ro.uaic.info.vros.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.dto.DeliveryRequest;
import ro.uaic.info.vros.dto.DeliveryResponse;
import ro.uaic.info.vros.exception.ResourceNotFoundException;
import ro.uaic.info.vros.repository.CustomerRepository;
import ro.uaic.info.vros.entity.Delivery;
import ro.uaic.info.vros.repository.DeliveryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryService {
    private final DeliveryRepository repository;
    private final CustomerRepository customers;

    public DeliveryService(DeliveryRepository repository, CustomerRepository customers) {
        this.repository = repository;
        this.customers = customers;
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public Delivery save(Delivery entity) {
        return repository.save(entity);
    }

    public List<Delivery> findAll() {
        return repository.findAll();
    }

    public Optional<Delivery> findById(Long id) {
        return repository.findById(id);
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repository.delete(requireDelivery(id));
    }

    public List<DeliveryResponse> getAll() {
        return repository.findAll(Sort.by("id")).stream().map(DeliveryResponse::from).toList();
    }

    public DeliveryResponse getById(Long id) {
        return DeliveryResponse.from(requireDelivery(id));
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @accessPolicy.isCustomer(#request.customerId()))")
    public DeliveryResponse create(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        applyRequest(delivery, request);
        return DeliveryResponse.from(repository.save(delivery));
    }

    // Verificare de rol aplicata si cand serviciul este apelat direct.
    @PreAuthorize("hasRole('ADMIN')")
    public DeliveryResponse update(Long id, DeliveryRequest request) {
        Delivery delivery = requireDelivery(id);
        applyRequest(delivery, request);
        return DeliveryResponse.from(repository.save(delivery));
    }

    private Delivery requireDelivery(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Delivery", id));
    }

    private void applyRequest(Delivery delivery, DeliveryRequest request) {
        delivery.setCustomer(customers.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId())));
        delivery.setQuantity(request.quantity());
        delivery.setNotes(request.notes());
    }
}
