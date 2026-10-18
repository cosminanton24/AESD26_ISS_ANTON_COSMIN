package ro.uaic.library.event;

import ro.uaic.library.domain.Member;

public record BorrowingThresholdExceededEvent(Member member, long borrowedCount, int threshold) { }
