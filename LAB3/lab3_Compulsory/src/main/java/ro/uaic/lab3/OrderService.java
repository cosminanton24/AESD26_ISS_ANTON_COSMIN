package ro.uaic.lab3;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final ProductCatalog catalog;

    // Spring assigns this field directly, after the constructor returns.
    @Autowired
    private InventoryService inventory;

    private PaymentService payment;
    private DeliveryService delivery;
    private NotificationService notification;

    // Snapshots of THIS bean, not of the construction order of its dependencies.
    private final List<InjectionSnapshot> injectionSnapshots = new ArrayList<>();

    // @Autowired is optional when a bean has just one constructor.
    public OrderService(ProductCatalog catalog) {
        this.catalog = catalog;
        recordSnapshot("CONSTRUCTOR");
        require(inventory == null && payment == null && delivery == null && notification == null,
                "Other injection points must still be null in the constructor.");
    }

    @Autowired
    public void setPaymentService(PaymentService payment) {
        // Check BEFORE assigning the setter dependency: field injection has already happened.
        require(inventory != null, "Field injection must precede setter injection.");
        this.payment = payment;
        recordSnapshot("SETTER");
    }

    @Autowired
    public void configureCheckout(DeliveryService delivery, NotificationService notification) {
        // An arbitrary injection method can accept multiple dependencies.
        require(inventory != null, "Field injection must precede method injection.");
        this.delivery = delivery;
        this.notification = notification;
        recordSnapshot("METHOD");
    }

    @PostConstruct
    void verifyInitialization() {
        require(catalog != null && inventory != null && payment != null
                        && delivery != null && notification != null,
                "All dependencies must be available before initialization completes.");
        recordSnapshot("POST_CONSTRUCT");
        System.out.println("[OK] Campul inventory era null in constructor si este injectat inainte de AMBELE metode.");
    }

    public String placeOrder() {
        return String.join(System.lineSeparator(),
                "Produs: " + catalog.productName(),
                inventory.reserve(catalog.productName()),
                payment.pay(), delivery.schedule(), notification.confirm());
    }

    public List<InjectionSnapshot> getInjectionSnapshots() {
        return List.copyOf(injectionSnapshots);
    }

    private void recordSnapshot(String stage) {
        InjectionSnapshot snapshot = new InjectionSnapshot(stage, catalog != null, inventory != null,
                payment != null, delivery != null, notification != null);
        injectionSnapshots.add(snapshot);
        System.out.printf("[%d] %-14s catalog=%s, inventory(field)=%s, payment(setter)=%s, delivery(method)=%s, notification(method)=%s%n",
                injectionSnapshots.size(), stage, snapshot.catalog(), snapshot.inventory(),
                snapshot.payment(), snapshot.delivery(), snapshot.notification());
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public record InjectionSnapshot(String stage, boolean catalog, boolean inventory,
                                    boolean payment, boolean delivery, boolean notification) {
    }
}
