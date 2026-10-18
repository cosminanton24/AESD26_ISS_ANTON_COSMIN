package ro.uaic.library.domain;

import java.time.Instant;

public record Loan(long memberId, long bookId, Instant borrowedAt) { }
