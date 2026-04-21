package common.protocol.response;

import common.protocol.AbstractResponse;

public final class InfoResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public InfoResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
