package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Spring construieste interogarea pornind de la numele metodei.
    Optional<Customer> findByEmail(String email);
}
