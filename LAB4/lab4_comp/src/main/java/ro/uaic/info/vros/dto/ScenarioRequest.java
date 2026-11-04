package ro.uaic.info.vros.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

// Scenariul primeste un nume si seturi de ID-uri pentru resurse deja existente.
// Seturile pot fi goale, dar nu null; fiecare ID trebuie sa fie pozitiv si nenul.
public record ScenarioRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull Set<@NotNull @Positive Long> vehicleIds,
        @NotNull Set<@NotNull @Positive Long> deliveryIds) {
}
