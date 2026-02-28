package util;

/**
 * Генератор уникальных ID для музыкальных групп
 */
public class IdGenerator {
    private static long counter = 0L;

    private IdGenerator() {
    }

    public static long nextId() {
        return ++counter;
    }

    /**
     * Синхронизирует генератор ID с максимальным ID в коллекции
     * 
     * @param maxId максимальный ID в коллекции
     */
    public static void syncTo(long maxId) {
        if (maxId >= counter)
            counter = maxId + 1;
    }
}