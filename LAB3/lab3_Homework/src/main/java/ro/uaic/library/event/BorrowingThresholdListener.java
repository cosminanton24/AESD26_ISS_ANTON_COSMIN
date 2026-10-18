package ro.uaic.library.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BorrowingThresholdListener {
    private static final Logger log = LoggerFactory.getLogger(BorrowingThresholdListener.class);
    @EventListener
    public void onThresholdExceeded(BorrowingThresholdExceededEvent event) {
        log.info("BORROWING ALERT: member={} books={} threshold={}", event.member().name(),
                event.borrowedCount(), event.threshold());
    }
}
