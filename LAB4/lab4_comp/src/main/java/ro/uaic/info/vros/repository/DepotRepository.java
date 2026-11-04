package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Depot;
import java.util.Optional;

public interface DepotRepository extends JpaRepository<Depot, Long> {
    Optional<Depot> findFirstByOrderByIdAsc();
}
