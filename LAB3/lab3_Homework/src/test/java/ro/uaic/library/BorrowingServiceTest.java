package ro.uaic.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import ro.uaic.library.domain.*;
import ro.uaic.library.event.BorrowingThresholdExceededEvent;
import ro.uaic.library.exception.BorrowingException;
import ro.uaic.library.policy.*;
import ro.uaic.library.repository.*;
import ro.uaic.library.service.BorrowingService;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import static org.assertj.core.api.Assertions.*;
import static ro.uaic.library.exception.BorrowingException.Reason.*;

@SpringBootTest(properties = {"library.demo.enabled=false", "library.borrowing.policy=fixed"})
@RecordApplicationEvents
@ExtendWith(OutputCaptureExtension.class)
class BorrowingServiceTest {
    private static final AtomicLong IDS = new AtomicLong(100);
    @Autowired BorrowingService service;
    @Autowired MemberRepository members;
    @Autowired BookRepository books;
    @Autowired LoanRepository loans;
    @Autowired BorrowingPolicy policy;
    @Autowired ApplicationEvents events;
    private long memberId;
    @BeforeEach void createMember() {
        memberId = IDS.incrementAndGet();
        members.save(new Member(memberId, "Test Member", MemberRole.REGULAR));
    }
    private long book() {
        long id = IDS.incrementAndGet();
        books.save(new Book(id, "Test Book " + id, "Test Author"));
        return id;
    }
    private void rejected(Runnable action, BorrowingException.Reason reason) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(BorrowingException.class,
                e -> assertThat(e.getReason()).isEqualTo(reason));
    }
    @Test void defaultsSelectFixedPolicy() { assertThat(policy).isInstanceOf(FixedBorrowingPolicy.class); }
    @Test void borrowReturnAndBorrowAgain(CapturedOutput output) {
        long bookId = book();
        Loan loan = service.borrowBook(memberId, bookId);
        assertThat(loan.borrowedAt()).isNotNull();
        assertThat(loans.countByMemberId(memberId)).isEqualTo(1);
        service.returnBook(memberId, bookId);
        assertThat(loans.findByBookId(bookId)).isEmpty();
        service.borrowBook(memberId, bookId);
        assertThat(output).contains("operation=borrowBook", "operation=returnBook", "member=Test Member",
                "book=Test Book", "executionTimeMs=", "success=true");
    }
    @Test void rejectsUnknownMemberAndBook(CapturedOutput output) {
        rejected(() -> service.borrowBook(-1, book()), MEMBER_NOT_FOUND);
        rejected(() -> service.borrowBook(memberId, -1), BOOK_NOT_FOUND);
        assertThat(loans.countByMemberId(memberId)).isZero();
        assertThat(output).contains("Failed operation=borrowBook", "does not exist", "success=false");
    }
    @Test void rejectsUnavailableCopy() {
        long id = book();
        service.borrowBook(memberId, id);
        rejected(() -> service.borrowBook(memberId, id), BOOK_UNAVAILABLE);
        assertThat(loans.countByMemberId(memberId)).isEqualTo(1);
    }
    @Test void enforcesLimitAndPublishesOnlySuccessfulAboveThresholdEvents(CapturedOutput output) {
        service.borrowBook(memberId, book());
        service.borrowBook(memberId, book());
        assertThat(events.stream(BorrowingThresholdExceededEvent.class)).isEmpty();
        long third = book();
        service.borrowBook(memberId, third);
        assertThat(events.stream(BorrowingThresholdExceededEvent.class)).singleElement().satisfies(e -> {
            assertThat(e.member().id()).isEqualTo(memberId);
            assertThat(e.borrowedCount()).isEqualTo(3);
            assertThat(e.threshold()).isEqualTo(2);
        });
        long fourth = book();
        rejected(() -> service.borrowBook(memberId, fourth), LIMIT_EXCEEDED);
        assertThat(loans.findByBookId(fourth)).isEmpty();
        assertThat(events.stream(BorrowingThresholdExceededEvent.class).count()).isEqualTo(1);
        assertThat(output).contains("BORROWING ALERT: member=Test Member books=3 threshold=2");
        service.returnBook(memberId, third);
        service.borrowBook(memberId, fourth);
        assertThat(events.stream(BorrowingThresholdExceededEvent.class).count()).isEqualTo(2);
    }
    @Test void rejectsWrongOwnerAndRepeatedReturn(CapturedOutput output) {
        long id = book();
        long other = IDS.incrementAndGet();
        members.save(new Member(other, "Other", MemberRole.STUDENT));
        service.borrowBook(memberId, id);
        rejected(() -> service.returnBook(other, id), INVALID_RETURN);
        assertThat(loans.findByBookId(id)).isPresent();
        service.returnBook(memberId, id);
        rejected(() -> service.returnBook(memberId, id), INVALID_RETURN);
        assertThat(output).contains("Failed operation=returnBook");
    }
    @Test void concurrentBorrowingCannotExceedLimit() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            var tasks = new java.util.ArrayList<Callable<Boolean>>();
            for (int i = 0; i < 12; i++) {
                long id = book();
                tasks.add(() -> {
                    try { service.borrowBook(memberId, id); return true; }
                    catch (BorrowingException e) { assertThat(e.getReason()).isEqualTo(LIMIT_EXCEEDED); return false; }
                });
            }
            int successes = 0;
            for (Future<Boolean> result : executor.invokeAll(tasks)) if (result.get()) successes++;
            assertThat(successes).isEqualTo(3);
            assertThat(loans.countByMemberId(memberId)).isEqualTo(3);
        } finally { executor.shutdownNow(); }
    }
}
