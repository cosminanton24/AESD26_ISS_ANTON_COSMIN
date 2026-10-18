package ro.uaic.lab3;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    public String reserve(String product) {
        return "Stoc rezervat pentru: " + product;
    }
}
