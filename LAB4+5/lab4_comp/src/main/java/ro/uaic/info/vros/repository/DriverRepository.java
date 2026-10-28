package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
