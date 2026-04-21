package common.protocol.response;

import common.protocol.AbstractResponse;

public final class ReplaceGreaterResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public ReplaceGreaterResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
