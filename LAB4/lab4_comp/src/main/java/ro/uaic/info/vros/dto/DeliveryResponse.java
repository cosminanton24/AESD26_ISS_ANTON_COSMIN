package ro.uaic.info.vros.dto;

import ro.uaic.info.vros.entity.Delivery;
import java.time.Instant;

// Raspunsul contine datele livrarii si doar ID-ul clientului asociat.
public record DeliveryResponse(Long id, Long customerId, int quantity, String notes, Instant timestamp) {
    // Extrage campurile necesare din entitatea JPA.
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(delivery.getId(), delivery.getCustomer().getId(),
                delivery.getQuantity(), delivery.getNotes(), delivery.getTimestamp());
    }
}
