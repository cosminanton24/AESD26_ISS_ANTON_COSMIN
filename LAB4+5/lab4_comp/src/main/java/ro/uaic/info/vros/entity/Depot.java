package ro.uaic.info.vros.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "depots")
public class Depot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String coordinates;

    // Legatura este salvata prin campul depot din Vehicle.
    @OneToMany(mappedBy = "depot")
    private List<Vehicle> vehicles = new ArrayList<>();

    public Depot() {
    }

    public Long getId() {
        return id;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
