package exception;

/**
 * Вызывается при достижении конца скрипта
 */
public class ScriptEndException extends RuntimeException {
    public ScriptEndException(String message) {
        super(message);
    }
}
