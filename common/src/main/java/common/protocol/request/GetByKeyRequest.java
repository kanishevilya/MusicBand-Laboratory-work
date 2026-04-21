package common.protocol.request;

import common.protocol.AbstractRequest;

public final class GetByKeyRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final long key;

    public GetByKeyRequest(long requestId, long key) {
        super(requestId);
        this.key = key;
    }

    public long getKey() {
        return key;
    }
}
