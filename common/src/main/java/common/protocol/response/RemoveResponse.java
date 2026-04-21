package common.protocol.response;

import common.protocol.AbstractResponse;

public final class RemoveResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public RemoveResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
