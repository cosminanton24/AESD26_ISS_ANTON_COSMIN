package ro.uaic.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class BorrowingExceptionAspect {
    private static final Logger log = LoggerFactory.getLogger(BorrowingExceptionAspect.class);
    @AfterThrowing(pointcut = "execution(public * ro.uaic.library.service.BorrowingService.*(..))", throwing = "error")
    public void logFailure(JoinPoint joinPoint, Throwable error) {
        log.error("Failed operation={} error={}", joinPoint.getSignature().getName(), error.getMessage());
    }
}
