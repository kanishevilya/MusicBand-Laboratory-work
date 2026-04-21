package common.protocol.response;

import common.protocol.AbstractResponse;

/**
 * Ответ об ошибке протокола или выполнения (не привязан к конкретной команде).
 */
public final class ErrorResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public ErrorResponse(long requestId, String message) {
        super(requestId, false, message);
    }
}
