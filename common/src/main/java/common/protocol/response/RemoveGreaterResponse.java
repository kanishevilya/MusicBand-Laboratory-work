package common.protocol.response;

import common.protocol.AbstractResponse;

public final class RemoveGreaterResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public RemoveGreaterResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
