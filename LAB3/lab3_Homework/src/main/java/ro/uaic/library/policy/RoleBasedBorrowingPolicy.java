package ro.uaic.library.policy;

import ro.uaic.library.domain.Member;

public record RoleBasedBorrowingPolicy(int studentLimit, int regularLimit, int premiumLimit)
        implements BorrowingPolicy {
    @Override public int limitFor(Member member) {
        return switch (member.role()) {
            case STUDENT -> studentLimit;
            case REGULAR -> regularLimit;
            case PREMIUM -> premiumLimit;
        };
    }
}
