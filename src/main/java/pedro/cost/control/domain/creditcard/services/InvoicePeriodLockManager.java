package pedro.cost.control.domain.creditcard.services;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class InvoicePeriodLockManager {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public Lock acquireMonth(Integer year, Integer month) {
        String key = "MONTH-" + year + "-" + month;
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        return lock;
    }

    public Lock acquireYear(Integer year) {
        String key = "YEAR-" + year;
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        return lock;
    }
}
