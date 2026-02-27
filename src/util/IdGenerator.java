package util;

public class IdGenerator {
    private static long counter = 0L;
    private IdGenerator() {}
    public static long nextId() {
        return ++counter;
    }
    public static void syncTo(long maxId) {
        if (maxId >= counter) counter = maxId + 1;
    }
}