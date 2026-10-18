package ro.uaic.lab3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Test
    void springInjectsConstructorThenFieldThenMethods() {
        var snapshots = orderService.getInjectionSnapshots();
        assertThat(snapshots).hasSize(4);

        var constructor = snapshots.get(0);
        assertThat(constructor.stage()).isEqualTo("CONSTRUCTOR");
        assertThat(constructor.catalog()).isTrue();
        assertThat(constructor.inventory()).isFalse();
        assertThat(constructor.payment()).isFalse();
        assertThat(constructor.delivery()).isFalse();
        assertThat(constructor.notification()).isFalse();

        // Setter and arbitrary method belong to the same phase; accept either order.
        assertThat(snapshots.subList(1, 3))
                .extracting(OrderService.InjectionSnapshot::stage)
                .containsExactlyInAnyOrder("SETTER", "METHOD");
        for (var snapshot : snapshots.subList(1, 3)) {
            assertThat(snapshot.catalog()).isTrue();
            assertThat(snapshot.inventory()).as("field available at %s", snapshot.stage()).isTrue();
            if (snapshot.stage().equals("SETTER")) {
                assertThat(snapshot.payment()).isTrue();
            } else {
                assertThat(snapshot.delivery()).isTrue();
                assertThat(snapshot.notification()).isTrue();
            }
        }

        var initialized = snapshots.get(3);
        assertThat(initialized.stage()).isEqualTo("POST_CONSTRUCT");
        assertThat(initialized.catalog() && initialized.inventory() && initialized.payment()
                && initialized.delivery() && initialized.notification()).isTrue();
    }

    @Test
    void orderUsesAllInjectedDependencies() {
        assertThat(orderService.placeOrder()).contains(
                "Produs: Tastatura", "Stoc rezervat pentru: Tastatura",
                "Plata simulata: acceptata", "Livrare simulata: programata",
                "Confirmare simulata: comanda inregistrata");
    }
}
