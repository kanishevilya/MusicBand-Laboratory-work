package exception;

/**
 * Вызывается при достижении конца скрипта
 */
public class CancelInputException extends RuntimeException {
    public CancelInputException(String message) {
        super(message);
    }
}
