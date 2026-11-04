package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.info.vros.entity.Scenario;

// Spring Data JPA ofera automat salvarea, cautarea si stergerea scenariilor.
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
