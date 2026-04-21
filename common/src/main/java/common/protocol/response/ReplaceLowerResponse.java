package common.protocol.response;

import common.protocol.AbstractResponse;

public final class ReplaceLowerResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public ReplaceLowerResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
