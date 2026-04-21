package common.protocol.request;

import common.model.MusicBand;
import common.protocol.AbstractRequest;

public final class ReplaceLowerRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final long key;
    private final MusicBand band;

    public ReplaceLowerRequest(long requestId, long key, MusicBand band) {
        super(requestId);
        this.key = key;
        this.band = band;
    }

    public long getKey() {
        return key;
    }

    public MusicBand getBand() {
        return band;
    }
}
