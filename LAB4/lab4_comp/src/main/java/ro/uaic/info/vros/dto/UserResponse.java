package ro.uaic.info.vros.dto;

import ro.uaic.info.vros.entity.Role;
import java.util.Set;

// Raspunsul nu contine niciodata parola sau hash-ul ei.
public record UserResponse(Long id, String email, Set<Role> roles, Long customerId, Long driverId) {
}
