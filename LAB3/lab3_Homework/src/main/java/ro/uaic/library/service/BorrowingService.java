package ro.uaic.library.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ro.uaic.library.config.BorrowingProperties;
import ro.uaic.library.domain.Loan;
import ro.uaic.library.event.BorrowingThresholdExceededEvent;
import ro.uaic.library.exception.BorrowingException;
import ro.uaic.library.repository.LoanRepository;
import java.time.Instant;
import static ro.uaic.library.exception.BorrowingException.Reason.*;

@Service
public class BorrowingService {
    private final LoanRepository loans;
    private final BorrowingValidator validator;
    private final ApplicationEventPublisher events;
    private final BorrowingProperties properties;
    public BorrowingService(LoanRepository loans, BorrowingValidator validator,
                            ApplicationEventPublisher events, BorrowingProperties properties) {
        this.loans = loans; this.validator = validator; this.events = events; this.properties = properties;
    }
    public Loan borrowBook(long memberId, long bookId) {
        // Also keeps policy validation intact for direct, non-Spring use of this service.
        validator.validateBorrow(memberId, bookId);
        Loan loan = new Loan(memberId, bookId, Instant.now());
        if (!loans.add(loan)) throw new BorrowingException(BOOK_UNAVAILABLE, "Book " + bookId + " is unavailable");
        long count = loans.countByMemberId(memberId);
        if (count > properties.eventThreshold()) {
            events.publishEvent(new BorrowingThresholdExceededEvent(validator.requireMember(memberId),
                    count, properties.eventThreshold()));
        }
        return loan;
    }
    public Loan returnBook(long memberId, long bookId) {
        validator.requireMember(memberId);
        validator.requireBook(bookId);
        Loan loan = loans.findByBookId(bookId).orElseThrow(() ->
                new BorrowingException(INVALID_RETURN, "Book " + bookId + " is not borrowed"));
        if (loan.memberId() != memberId)
            throw new BorrowingException(INVALID_RETURN, "Book " + bookId + " was borrowed by another member");
        if (!loans.remove(loan)) throw new BorrowingException(INVALID_RETURN, "Book was already returned");
        return loan;
    }
}
