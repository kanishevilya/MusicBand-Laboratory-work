package common.util;

import java.util.concurrent.atomic.AtomicLong;

public class RequestIdGenerator {
    private static final AtomicLong counter = new AtomicLong(1);

    public static long nextId() {
        return counter.getAndIncrement();
    }
}