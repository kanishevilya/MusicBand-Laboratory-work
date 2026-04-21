package common.protocol.request;

import common.model.MusicBand;
import common.protocol.AbstractRequest;

public final class UpdateRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final long id;
    private final MusicBand band;

    public UpdateRequest(long requestId, long id, MusicBand band) {
        super(requestId);
        this.id = id;
        this.band = band;
    }

    public long getId() {
        return id;
    }

    public MusicBand getBand() {
        return band;
    }
}
