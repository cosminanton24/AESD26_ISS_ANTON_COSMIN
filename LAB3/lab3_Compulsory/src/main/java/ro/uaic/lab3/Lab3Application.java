package ro.uaic.lab3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }

    @Bean
    CommandLineRunner demonstrateOrder(OrderService orderService) {
        return args -> {
            System.out.println("\n=== COMANDA ONLINE ===");
            System.out.println(orderService.placeOrder());
            System.out.println("\nOrdine demonstrata: constructor -> field -> setter & method -> @PostConstruct");
        };
    }
}
