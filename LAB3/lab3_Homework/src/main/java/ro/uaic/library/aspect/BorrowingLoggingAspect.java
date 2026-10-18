package ro.uaic.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ro.uaic.library.repository.*;

@Aspect
@Component
@Order(1)
public class BorrowingLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(BorrowingLoggingAspect.class);
    private final MemberRepository members;
    private final BookRepository books;
    public BorrowingLoggingAspect(MemberRepository members, BookRepository books) {
        this.members = members; this.books = books;
    }
    @Around("execution(public * ro.uaic.library.service.BorrowingService.*(..)) && args(memberId,bookId)")
    public Object logOperation(ProceedingJoinPoint joinPoint, long memberId, long bookId) throws Throwable {
        long start = System.nanoTime();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            String member = members.findById(memberId).map(m -> m.name()).orElse("unknown #" + memberId);
            String book = books.findById(bookId).map(b -> b.title()).orElse("unknown #" + bookId);
            log.info("operation={} member={} book={} executionTimeMs={} success={}",
                    joinPoint.getSignature().getName(), member, book,
                    (System.nanoTime() - start) / 1_000_000.0, success);
        }
    }
}
