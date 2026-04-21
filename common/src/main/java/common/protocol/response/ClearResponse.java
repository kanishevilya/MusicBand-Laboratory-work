package common.protocol.response;

import common.protocol.AbstractResponse;

public final class ClearResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public ClearResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
