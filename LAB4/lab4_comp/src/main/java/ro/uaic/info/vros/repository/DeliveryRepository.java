package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
