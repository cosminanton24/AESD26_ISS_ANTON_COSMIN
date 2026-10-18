package ro.uaic.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ro.uaic.library.service.LibraryOperationsLock;

@Aspect
@Component
@Order(0)
public class OperationSerializationAspect {
    private final LibraryOperationsLock lock;
    public OperationSerializationAspect(LibraryOperationsLock lock) { this.lock = lock; }
    @Around("execution(public * ro.uaic.library.service.BorrowingService.*(..))")
    public Object serialize(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try { return joinPoint.proceed(); }
        finally { lock.unlock(); }
    }
}
