package ro.uaic.info.vros.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

// Un scenariu grupeaza resursele selectate pentru o viitoare optimizare a rutelor.
@Entity
@Table(name = "scenarios")
public class Scenario {
    // PostgreSQL genereaza automat identificatorul scenariului.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numele scenariului este obligatoriu si are maximum 100 de caractere.
    @Column(nullable = false, length = 100)
    private String name;

    // Many-to-many prin scenario_vehicles: un vehicul poate fi inclus in mai multe scenarii.
    @ManyToMany
    @JoinTable(name = "scenario_vehicles",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles = new HashSet<>();

    // Many-to-many prin scenario_deliveries: o livrare poate fi inclusa in mai multe scenarii.
    @ManyToMany
    @JoinTable(name = "scenario_deliveries",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_id"))
    private Set<Delivery> deliveries = new HashSet<>();

    public Scenario() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Set<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Set<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
}
