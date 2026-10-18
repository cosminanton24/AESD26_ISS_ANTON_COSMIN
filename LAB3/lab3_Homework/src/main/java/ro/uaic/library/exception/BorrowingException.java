package ro.uaic.library.exception;

public class BorrowingException extends RuntimeException {
    public enum Reason { MEMBER_NOT_FOUND, BOOK_NOT_FOUND, BOOK_UNAVAILABLE, LIMIT_EXCEEDED, INVALID_RETURN }
    private final Reason reason;
    public BorrowingException(Reason reason, String message) { super(message); this.reason = reason; }
    public Reason getReason() { return reason; }
}
