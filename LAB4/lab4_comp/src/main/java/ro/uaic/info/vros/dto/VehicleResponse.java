package ro.uaic.info.vros.dto;

import ro.uaic.info.vros.entity.Vehicle;

// Datele vehiculului trimise clientului API, cu ID-ul depozitului in locul entitatii JPA.
public record VehicleResponse(Long id, String name, int capacity, boolean available, Long depotId) {
    // Transforma entitatea persistenta intr-un DTO pentru raspuns.
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(vehicle.getId(), vehicle.getName(), vehicle.getCapacity(),
                vehicle.isAvailable(), vehicle.getDepot().getId());
    }
}
