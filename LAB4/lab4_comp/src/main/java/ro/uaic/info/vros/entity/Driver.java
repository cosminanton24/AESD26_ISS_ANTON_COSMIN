package ro.uaic.info.vros.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver extends Person {
    public Driver() {
    }
}
