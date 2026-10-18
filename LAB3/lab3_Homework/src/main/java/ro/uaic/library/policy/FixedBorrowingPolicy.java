package ro.uaic.library.policy;

import ro.uaic.library.domain.Member;

public record FixedBorrowingPolicy(int limit) implements BorrowingPolicy {
    @Override public int limitFor(Member member) { return limit; }
}
