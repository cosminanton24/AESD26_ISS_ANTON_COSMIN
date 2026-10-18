package ro.uaic.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.uaic.library.domain.*;
import ro.uaic.library.exception.BorrowingException;
import ro.uaic.library.policy.*;
import ro.uaic.library.repository.*;
import ro.uaic.library.service.BorrowingService;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"library.demo.enabled=false", "library.borrowing.policy=role-based",
        "library.borrowing.student-limit=1", "library.borrowing.regular-limit=2", "library.borrowing.premium-limit=4"})
class RoleBasedPolicyTest {
    @Autowired BorrowingPolicy policy;
    @Autowired BorrowingService service;
    @Autowired MemberRepository members;
    @Autowired BookRepository books;
    @Test void selectsAndEnforcesConfiguredLimitsForEveryRole() {
        assertThat(policy).isInstanceOf(RoleBasedBorrowingPolicy.class);
        int[] limits = {1, 2, 4};
        for (MemberRole role : MemberRole.values()) {
            long memberId = role.ordinal() + 1;
            Member member = members.save(new Member(memberId, role.name(), role));
            int limit = limits[role.ordinal()];
            assertThat(policy.limitFor(member)).isEqualTo(limit);
            for (int i = 0; i <= limit; i++) {
                long bookId = memberId * 100 + i;
                books.save(new Book(bookId, "Book " + bookId, "Author"));
                if (i < limit) service.borrowBook(memberId, bookId);
                else assertThatThrownBy(() -> service.borrowBook(memberId, bookId))
                        .isInstanceOfSatisfying(BorrowingException.class,
                                e -> assertThat(e.getReason()).isEqualTo(BorrowingException.Reason.LIMIT_EXCEEDED));
            }
        }
    }
}
