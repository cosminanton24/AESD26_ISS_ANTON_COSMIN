package ro.uaic.library;

import org.slf4j.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ro.uaic.library.domain.*;
import ro.uaic.library.exception.BorrowingException;
import ro.uaic.library.policy.BorrowingPolicy;
import ro.uaic.library.repository.*;
import ro.uaic.library.service.BorrowingService;

@Component
@ConditionalOnProperty(name = "library.demo.enabled", havingValue = "true")
public class DemoRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);
    private final MemberRepository members;
    private final BookRepository books;
    private final BorrowingService service;
    private final BorrowingPolicy policy;
    public DemoRunner(MemberRepository members, BookRepository books, BorrowingService service, BorrowingPolicy policy) {
        this.members = members; this.books = books; this.service = service; this.policy = policy;
    }
    @Override public void run(String... args) {
        Member member = members.save(new Member(1, "Cosmin Anton", MemberRole.PREMIUM));
        members.save(new Member(2, "Ana Popescu", MemberRole.STUDENT));
        members.save(new Member(3, "Mihai Ionescu", MemberRole.REGULAR));
        String[] titles = {"Clean Code", "Effective Java", "Design Patterns", "Refactoring", "Java Concurrency in Practice", "Spring in Action"};
        for (int i = 0; i < titles.length; i++) books.save(new Book(i + 1, titles[i], "Demo author"));
        log.info("Selected policy={} premiumLimit={}", policy.getClass().getSimpleName(), policy.limitFor(member));
        for (int i = 1; i <= Math.min(policy.limitFor(member) + 1, titles.length); i++) {
            final int id = i;
            attempt(() -> service.borrowBook(1, id));
        }
        attempt(() -> service.borrowBook(2, 1));
        attempt(() -> service.borrowBook(999, 1));
        attempt(() -> service.returnBook(2, 1));
        service.returnBook(1, 1);
        service.borrowBook(2, 1);
        log.info("Demo complete: book 1 was returned and borrowed by Ana Popescu.");
    }
    private void attempt(Runnable operation) {
        try { operation.run(); }
        catch (BorrowingException error) { log.info("Expected demo rejection: {}", error.getReason()); }
    }
}
