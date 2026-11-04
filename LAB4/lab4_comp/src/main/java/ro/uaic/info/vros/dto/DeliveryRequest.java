package ro.uaic.info.vros.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Clientul si cantitatea trebuie specificate si sa fie pozitive; notitele sunt optionale.
// Data livrarii este generata de aplicatie la creare, nu este primita in acest DTO.
public record DeliveryRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Integer quantity,
        String notes) {
}
