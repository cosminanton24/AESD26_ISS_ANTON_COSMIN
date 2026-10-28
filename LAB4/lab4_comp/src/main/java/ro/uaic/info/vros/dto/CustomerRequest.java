package ro.uaic.info.vros.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datele primite prin POST si PUT, fara ID sau relatii JPA.
public record CustomerRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 100) String coordinates) {
}
