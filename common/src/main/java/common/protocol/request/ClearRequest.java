package common.protocol.request;

import common.protocol.AbstractRequest;

public final class ClearRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    public ClearRequest(long requestId) {
        super(requestId);
    }
}
