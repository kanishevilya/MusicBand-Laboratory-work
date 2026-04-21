package common.protocol.request;

import common.model.MusicBand;
import common.protocol.AbstractRequest;

public final class RemoveGreaterRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final MusicBand referenceBand;

    public RemoveGreaterRequest(long requestId, MusicBand referenceBand) {
        super(requestId);
        this.referenceBand = referenceBand;
    }

    public MusicBand getReferenceBand() {
        return referenceBand;
    }
}
