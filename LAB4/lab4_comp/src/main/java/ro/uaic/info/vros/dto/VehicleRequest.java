package ro.uaic.info.vros.dto;

import jakarta.validation.constraints.*;

// Datele primite la creare/actualizare; depozitul este ales de serviciu, fara sofer.
// Validarea impune un nume completat, capacitate pozitiva si disponibilitate specificata.
public record VehicleRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Positive Integer capacity,
        @NotNull Boolean available) {
}
