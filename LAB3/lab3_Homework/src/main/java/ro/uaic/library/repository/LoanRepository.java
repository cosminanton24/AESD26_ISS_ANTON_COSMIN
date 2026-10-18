package ro.uaic.library.repository;

import org.springframework.stereotype.Repository;
import ro.uaic.library.domain.Loan;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LoanRepository {
    private final ConcurrentHashMap<Long, Loan> loans = new ConcurrentHashMap<>();
    public Optional<Loan> findByBookId(long bookId) { return Optional.ofNullable(loans.get(bookId)); }
    public long countByMemberId(long memberId) {
        return loans.values().stream().filter(loan -> loan.memberId() == memberId).count();
    }
    public List<Loan> findByMemberId(long memberId) {
        return loans.values().stream().filter(loan -> loan.memberId() == memberId).toList();
    }
    public boolean add(Loan loan) { return loans.putIfAbsent(loan.bookId(), loan) == null; }
    public boolean remove(Loan loan) { return loans.remove(loan.bookId(), loan); }
}
