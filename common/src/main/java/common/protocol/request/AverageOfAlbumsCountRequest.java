package common.protocol.request;

import common.protocol.AbstractRequest;

public final class AverageOfAlbumsCountRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    public AverageOfAlbumsCountRequest(long requestId) {
        super(requestId);
    }
}
