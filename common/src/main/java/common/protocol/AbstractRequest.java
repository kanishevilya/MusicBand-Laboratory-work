package common.protocol;

import java.io.Serializable;

/**
 * Базовый тип запроса клиента к серверу.
 */
public abstract class AbstractRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long requestId;

    protected AbstractRequest(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }
}
