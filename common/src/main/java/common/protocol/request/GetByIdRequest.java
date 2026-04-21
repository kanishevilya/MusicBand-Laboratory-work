package common.protocol.request;

import common.protocol.AbstractRequest;

public final class GetByIdRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final long bandId;

    public GetByIdRequest(long requestId, long bandId) {
        super(requestId);
        this.bandId = bandId;
    }

    public long getBandId() {
        return bandId;
    }
}
