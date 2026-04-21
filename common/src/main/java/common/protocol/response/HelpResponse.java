package common.protocol.response;

import common.protocol.AbstractResponse;

public final class HelpResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    public HelpResponse(long requestId, boolean success, String message) {
        super(requestId, success, message);
    }
}
