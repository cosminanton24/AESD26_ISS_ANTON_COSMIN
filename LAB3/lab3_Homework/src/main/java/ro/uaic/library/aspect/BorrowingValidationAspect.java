package ro.uaic.library.aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ro.uaic.library.service.BorrowingValidator;

@Aspect
@Component
@Order(3)
public class BorrowingValidationAspect {
    private final BorrowingValidator validator;
    public BorrowingValidationAspect(BorrowingValidator validator) { this.validator = validator; }
    @Before("execution(* ro.uaic.library.service.BorrowingService.borrowBook(long,long)) && args(memberId,bookId)")
    public void validate(long memberId, long bookId) { validator.validateBorrow(memberId, bookId); }
}
