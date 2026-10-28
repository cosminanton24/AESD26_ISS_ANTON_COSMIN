package ro.uaic.info.vros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.entity.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // JPQL explicit: vehicule disponibile dintr-un depozit, cu capacitate suficienta.
    @Query("SELECT v FROM Vehicle v WHERE v.depot.id = :depotId "
            + "AND v.available = true AND v.capacity >= :quantity")
    List<Vehicle> findAvailableVehicles(@Param("depotId") Long depotId,
                                       @Param("quantity") int quantity);

    // Actualizare directa, executata obligatoriu intr-o tranzactie.
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Vehicle v SET v.available = :available WHERE v.id = :id")
    int updateAvailability(@Param("id") Long id, @Param("available") boolean available);
}
