package ro.uaic.library.service;

import org.springframework.stereotype.Component;
import ro.uaic.library.domain.*;
import ro.uaic.library.exception.BorrowingException;
import ro.uaic.library.policy.BorrowingPolicy;
import ro.uaic.library.repository.*;
import static ro.uaic.library.exception.BorrowingException.Reason.*;

@Component
public class BorrowingValidator {
    private final MemberRepository members;
    private final BookRepository books;
    private final LoanRepository loans;
    private final BorrowingPolicy policy;
    public BorrowingValidator(MemberRepository members, BookRepository books,
                             LoanRepository loans, BorrowingPolicy policy) {
        this.members = members; this.books = books; this.loans = loans; this.policy = policy;
    }
    public Member requireMember(long id) {
        return members.findById(id).orElseThrow(() -> new BorrowingException(MEMBER_NOT_FOUND, "Member " + id + " does not exist"));
    }
    public Book requireBook(long id) {
        return books.findById(id).orElseThrow(() -> new BorrowingException(BOOK_NOT_FOUND, "Book " + id + " does not exist"));
    }
    public void validateBorrow(long memberId, long bookId) {
        Member member = requireMember(memberId);
        Book book = requireBook(bookId);
        if (loans.findByBookId(bookId).isPresent())
            throw new BorrowingException(BOOK_UNAVAILABLE, "Book '" + book.title() + "' is unavailable");
        if (!policy.canBorrow(member, loans.countByMemberId(memberId)))
            throw new BorrowingException(LIMIT_EXCEEDED, member.name() + " has reached the borrowing limit of " + policy.limitFor(member));
    }
}
