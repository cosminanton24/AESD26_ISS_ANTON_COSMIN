package ro.uaic.library.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.locks.ReentrantLock;

/** Shared by the outer aspect: validation and mutation execute as one atomic operation. */
@Component
public class LibraryOperationsLock {
    private final ReentrantLock lock = new ReentrantLock();
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }
}
