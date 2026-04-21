package common.protocol.response;

import common.protocol.AbstractResponse;

public final class InsertResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public InsertResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
