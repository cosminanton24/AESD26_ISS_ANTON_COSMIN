package ro.uaic.info.vros.dto;

import jakarta.validation.constraints.*;
import ro.uaic.info.vros.entity.Role;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 30) String phone,
        @NotNull Role role,
        @Size(max = 500) String address,
        @Size(max = 100) String coordinates) {
}
