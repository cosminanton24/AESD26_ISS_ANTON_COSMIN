package ro.uaic.info.vros.dto;

import ro.uaic.info.vros.entity.Scenario;
import java.util.List;

// Expune scenariul prin date simple si ID-uri, fara obiectele JPA asociate.
public record ScenarioResponse(Long id, String name, List<Long> vehicleIds, List<Long> deliveryIds) {
    public static ScenarioResponse from(Scenario scenario) {
        // Ordinea stabila a ID-urilor pastreaza stabil si ETag-ul reprezentarii.
        return new ScenarioResponse(scenario.getId(), scenario.getName(),
                scenario.getVehicles().stream().map(v -> v.getId()).sorted().toList(),
                scenario.getDeliveries().stream().map(d -> d.getId()).sorted().toList());
    }
}
