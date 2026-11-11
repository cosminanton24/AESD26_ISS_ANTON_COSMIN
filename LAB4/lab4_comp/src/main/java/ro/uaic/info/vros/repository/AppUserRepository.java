package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.AppUser;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}
