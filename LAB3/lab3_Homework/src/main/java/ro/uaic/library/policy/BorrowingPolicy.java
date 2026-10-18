package ro.uaic.library.policy;

import ro.uaic.library.domain.Member;

public interface BorrowingPolicy {
    int limitFor(Member member);
    default boolean canBorrow(Member member, long currentLoans) {
        return currentLoans < limitFor(member);
    }
}
