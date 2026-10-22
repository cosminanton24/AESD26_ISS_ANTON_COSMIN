package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
