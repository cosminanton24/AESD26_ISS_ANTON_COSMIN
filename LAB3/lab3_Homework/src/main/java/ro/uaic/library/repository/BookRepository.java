package ro.uaic.library.repository;

import org.springframework.stereotype.Repository;
import ro.uaic.library.domain.Book;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookRepository {
    private final ConcurrentHashMap<Long, Book> books = new ConcurrentHashMap<>();
    public Book save(Book book) { books.put(book.id(), book); return book; }
    public Optional<Book> findById(long id) { return Optional.ofNullable(books.get(id)); }
    public List<Book> findAll() { return List.copyOf(books.values()); }
}
