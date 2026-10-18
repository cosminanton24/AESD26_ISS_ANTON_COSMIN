package ro.uaic.library.domain;

/** One book represents one physical copy. Loan ownership is stored in the loan repository. */
public record Book(long id, String title, String author) {
    public Book {
        if (id <= 0) throw new IllegalArgumentException("Book ID must be positive");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Book title is required");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Book author is required");
    }
}
