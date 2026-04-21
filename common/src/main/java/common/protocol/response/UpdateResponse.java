package common.protocol.response;

import common.protocol.AbstractResponse;

public final class UpdateResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public UpdateResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
