package common.protocol;

import java.io.Serializable;

/**
 * Базовый тип ответа сервера клиенту.
 */
public abstract class AbstractResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long requestId;
    private final boolean success;
    private final String message;

    protected AbstractResponse(long requestId, boolean success, String message) {
        this.requestId = requestId;
        this.success = success;
        this.message = message == null ? "" : message;
    }

    public long getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
