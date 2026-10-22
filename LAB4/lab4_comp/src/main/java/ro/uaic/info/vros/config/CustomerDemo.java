package ro.uaic.info.vros.config;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ro.uaic.info.vros.entity.Customer;
import ro.uaic.info.vros.repository.CustomerRepository;

import java.util.UUID;

// Spring gestioneaza clasa si activeaza testul conform configuratiei.
@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true", matchIfMissing = true)
public class CustomerDemo implements CommandLineRunner {
    // Instrumentele folosite pentru afisare, acces la date si tranzactii.
    private static final Logger log = LoggerFactory.getLogger(CustomerDemo.class);
    private final CustomerRepository customers;
    private final TransactionTemplate transactions;
    private final EntityManager entityManager;

    // Spring furnizeaza automat dependentele prin constructor.
    public CustomerDemo(CustomerRepository customers, TransactionTemplate transactions,
                        EntityManager entityManager) {
        this.customers = customers;
        this.transactions = transactions;
        this.entityManager = entityManager;
    }

    // Spring executa testul automat dupa pornirea aplicatiei.
    @Override
    public void run(String... args) {
        // Toate operatiile testului se executa in aceeasi tranzactie.
        transactions.executeWithoutResult(status -> {
            // Anuleaza modificarile la final, inclusiv daca testul esueaza.
            status.setRollbackOnly();
            // Retine numarul de clienti existenti inainte de test.
            long initialCount = customers.count();
            // Creeaza un client fictiv cu email unic pentru aceasta rulare.
            Customer customer = new Customer();
            customer.setName("Demo customer");
            customer.setEmail("demo-" + UUID.randomUUID() + "@example.com");
            customer.setPhone("+40700000000");
            customer.setAddress("Strada Palat 1, Iasi");
            customer.setCoordinates("47.1585,27.6014");

            // CREATE: salveaza clientul si trimite inserarea catre PostgreSQL.
            Customer saved = customers.saveAndFlush(customer);
            // Verifica generarea ID-ului si cresterea numarului de clienti.
            check(saved.getId() != null, "The customer ID must be generated");
            check(customers.count() == initialCount + 1, "Insert failed");
            // Goleste memoria JPA pentru a reciti clientul din baza de date.
            entityManager.clear();
            // READ: cauta dupa email si verifica identitatea clientului gasit.
            Customer found = customers.findByEmail(saved.getEmail()).orElseThrow();
            check(found.getId().equals(saved.getId()), "Read failed");

            // UPDATE: modifica adresa si trimite actualizarea catre baza de date.
            found.setAddress("Strada Universitatii 16, Iasi");
            customers.saveAndFlush(found);
            // Reciteste clientul din baza si verifica noua adresa.
            entityManager.clear();
            Customer updated = customers.findById(saved.getId()).orElseThrow();
            check(updated.getAddress()
                    .equals("Strada Universitatii 16, Iasi"), "Update failed");

            // DELETE: sterge clientul si trimite stergerea catre baza de date.
            customers.delete(updated);
            customers.flush();
            // Verifica disparitia clientului si revenirea la numarul initial.
            entityManager.clear();
            check(customers.findById(saved.getId()).isEmpty(), "Delete failed");
            check(customers.count() == initialCount, "Unexpected final customer count");
            // Afiseaza succesul doar daca toate verificarile au trecut.
            log.info("Customer CRUD test PASSED (create, read, update, delete).");
        });
    }

    // O verificare esuata arunca o exceptie si opreste testul.
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
