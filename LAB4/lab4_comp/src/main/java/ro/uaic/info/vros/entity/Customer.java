package ro.uaic.info.vros.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends Person {
    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String coordinates;

    // Legatura este salvata prin campul customer din Delivery.
    @OneToMany(mappedBy = "customer")
    private List<Delivery> deliveries = new ArrayList<>();

    public Customer() {
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
